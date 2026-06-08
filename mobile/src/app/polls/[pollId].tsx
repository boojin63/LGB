import { useFocusEffect, useLocalSearchParams, useRouter } from 'expo-router';
import { useCallback, useState } from 'react';
import { Button, Platform, Pressable, ScrollView, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { getPoll, getPollResult, PollDetail, PollResult, votePoll } from '@/api/polls';
import { ApiError } from '@/api/types';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useAuth } from '@/hooks/useAuth';

export default function PollDetailScreen() {
  const router = useRouter();
  const theme = useTheme();
  const params = useLocalSearchParams<{ pollId?: string | string[] }>();
  const { isAuthenticated, isLoading, role } = useAuth();
  const [poll, setPoll] = useState<PollDetail | null>(null);
  const [result, setResult] = useState<PollResult | null>(null);
  const [selectedOptionId, setSelectedOptionId] = useState<number | null>(null);
  const [isPollLoading, setIsPollLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [resultMessage, setResultMessage] = useState<string | null>(null);

  const pollId = parsePollId(params.pollId);

  const loadPoll = useCallback(async () => {
    if (!pollId) {
      setPoll(null);
      setErrorMessage('Invalid poll ID.');
      setIsPollLoading(false);
      return;
    }

    setIsPollLoading(true);
    setErrorMessage(null);
    setResultMessage(null);

    try {
      const response = await getPoll(pollId);
      setPoll(response);
      setSelectedOptionId(response.options[0]?.id ?? null);
    } catch (error) {
      setPoll(null);

      if (error instanceof ApiError) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage('Failed to load poll detail.');
      }
    } finally {
      setIsPollLoading(false);
    }
  }, [pollId]);

  useFocusEffect(
    useCallback(() => {
      if (isLoading) {
        return;
      }

      if (!isAuthenticated) {
        router.replace('/login');
        return;
      }

      void loadPoll();
    }, [isAuthenticated, isLoading, loadPoll, router]),
  );

  async function handleVote() {
    if (!pollId || !selectedOptionId) {
      setErrorMessage('Select an option.');
      return;
    }

    setIsSubmitting(true);
    setErrorMessage(null);

    try {
      await votePoll(pollId, selectedOptionId);
      await loadPoll();
      await handleLoadResult();
    } catch (error) {
      if (error instanceof ApiError) {
        if (error.status === 409) {
          setErrorMessage('이미 투표에 참여했습니다. 결과 확인을 눌러 확인해보세요.');
        } else {
          setErrorMessage(error.message);
        }
      } else {
        setErrorMessage('투표를 제출하지 못했습니다.');
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  async function handleLoadResult() {
    if (!pollId) {
      return;
    }

    setResultMessage(null);

    try {
      const response = await getPollResult(pollId);
      setResult(response);
    } catch (error) {
      setResult(null);

      if (error instanceof ApiError && error.status === 403) {
        setResultMessage('아직 결과를 볼 수 없습니다.');
        return;
      }

      if (error instanceof ApiError) {
        setResultMessage(error.message);
      } else {
        setResultMessage('투표 결과를 불러오지 못했습니다.');
      }
    }
  }

  function handleBack() {
    if (router.canGoBack()) {
      router.back();
      return;
    }

    if (Platform.OS === 'web') {
      window.location.assign('/polls');
      return;
    }

    router.replace('/polls/index' as never);
  }

  if (isLoading || isPollLoading) {
    return (
      <ThemedView style={styles.container}>
        <SafeAreaView style={styles.safeArea}>
          <ThemedText type="subtitle">투표를 불러오는 중...</ThemedText>
        </SafeAreaView>
      </ThemedView>
    );
  }

  const canVote = role === 'STUDENT' && poll?.status === 'OPEN' && !poll.hasVoted;

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ScrollView contentContainerStyle={styles.content}>
          <ThemedView style={styles.buttonWrap}>
            <Button title="Back" onPress={handleBack} />
          </ThemedView>

          {errorMessage && (
            <ThemedView type="backgroundElement" style={styles.messageBox}>
              <ThemedText type="small" style={styles.errorText}>
                {errorMessage}
              </ThemedText>
            </ThemedView>
          )}

          {poll && (
            <>
              <ThemedText type="subtitle">{poll.title}</ThemedText>
              <ThemedView type="backgroundElement" style={styles.card}>
                <ThemedText>{poll.description ?? 'No description'}</ThemedText>
                <ThemedText type="small">Status: {poll.status}</ThemedText>
                <ThemedText type="small">Starts: {formatDateTime(poll.startsAt)}</ThemedText>
                <ThemedText type="small">Ends: {formatDateTime(poll.endsAt)}</ThemedText>
                <ThemedText type="small">Voted: {poll.hasVoted ? 'Yes' : 'No'}</ThemedText>
              </ThemedView>

              <ThemedView style={styles.options}>
                {poll.options.map((option) => {
                  const selected = option.id === selectedOptionId;
                  return (
                    <Pressable
                      key={option.id}
                      disabled={!canVote}
                      style={({ pressed }) => [
                        styles.option,
                        { backgroundColor: selected ? '#2563eb' : theme.backgroundElement },
                        pressed && styles.pressed,
                      ]}
                      onPress={() => setSelectedOptionId(option.id)}>
                      <ThemedText
                        type="smallBold"
                        style={selected ? styles.selectedText : undefined}>
                        {option.text}
                      </ThemedText>
                    </Pressable>
                  );
                })}
              </ThemedView>

              {role === 'ADMIN' && (
                <ThemedView type="backgroundElement" style={styles.messageBox}>
                  <ThemedText type="small">관리자는 투표할 수 없습니다.</ThemedText>
                </ThemedView>
              )}

              {role === 'STUDENT' && poll.hasVoted && (
                <ThemedView type="backgroundElement" style={styles.messageBox}>
                  <ThemedText type="small">이미 투표에 참여했습니다.</ThemedText>
                </ThemedView>
              )}

              {canVote && (
                <ThemedView style={styles.buttonWrap}>
                  <Button
                    title={isSubmitting ? '제출 중...' : '투표하기'}
                    onPress={handleVote}
                    disabled={isSubmitting}
                  />
                </ThemedView>
              )}

              <ThemedView style={styles.buttonWrap}>
                <Button title="결과 확인" onPress={handleLoadResult} />
              </ThemedView>

              {resultMessage && (
                <ThemedView type="backgroundElement" style={styles.messageBox}>
                  <ThemedText type="small">{resultMessage}</ThemedText>
                </ThemedView>
              )}

              {result && (
                <ThemedView type="backgroundElement" style={styles.card}>
                  <ThemedText type="smallBold">Results</ThemedText>
                  <ThemedText type="small">Total votes: {result.totalVotes}</ThemedText>
                  {result.options.map((option) => (
                    <ThemedText key={option.optionId} type="small">
                      {option.text}: {option.voteCount} ({option.percentage.toFixed(1)}%)
                    </ThemedText>
                  ))}
                </ThemedView>
              )}
            </>
          )}
        </ScrollView>
      </SafeAreaView>
    </ThemedView>
  );
}

function parsePollId(value: string | string[] | undefined): number | null {
  const rawValue = Array.isArray(value) ? value[0] : value;

  if (!rawValue) {
    return null;
  }

  const parsed = Number(rawValue);

  if (!Number.isInteger(parsed) || parsed <= 0) {
    return null;
  }

  return parsed;
}

function formatDateTime(value: string): string {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString();
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    flexDirection: 'row',
    justifyContent: 'center',
  },
  safeArea: {
    flex: 1,
    maxWidth: MaxContentWidth,
    paddingBottom: BottomTabInset + Spacing.three,
  },
  content: {
    gap: Spacing.three,
    padding: Spacing.four,
  },
  card: {
    borderRadius: Spacing.three,
    gap: Spacing.one,
    padding: Spacing.three,
  },
  options: {
    gap: Spacing.two,
  },
  option: {
    borderRadius: Spacing.three,
    padding: Spacing.three,
  },
  selectedText: {
    color: '#ffffff',
  },
  buttonWrap: {
    borderRadius: Spacing.three,
    overflow: 'hidden',
  },
  messageBox: {
    borderRadius: Spacing.three,
    padding: Spacing.three,
  },
  errorText: {
    color: '#dc2626',
  },
  pressed: {
    opacity: 0.75,
  },
});
