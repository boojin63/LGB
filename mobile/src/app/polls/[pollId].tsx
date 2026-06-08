import { useFocusEffect, useLocalSearchParams, useRouter } from 'expo-router';
import { useCallback, useState } from 'react';
import { Platform, Pressable, ScrollView, StyleSheet } from 'react-native';
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
      setErrorMessage('This poll link is not valid.');
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
        setErrorMessage('Poll detail could not be loaded.');
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
      setErrorMessage('Select one option before voting.');
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
          setErrorMessage('You already voted in this poll. Open results to check what is available.');
        } else {
          setErrorMessage(error.message);
        }
      } else {
        setErrorMessage('Your vote could not be submitted. Please try again.');
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
        setResultMessage('Results are not visible yet. Please check again after the poll is closed or published.');
        return;
      }

      if (error instanceof ApiError) {
        setResultMessage(error.message);
      } else {
        setResultMessage('Poll results could not be loaded.');
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
          <ThemedText type="subtitle">Loading poll...</ThemedText>
        </SafeAreaView>
      </ThemedView>
    );
  }

  const canVote = role === 'STUDENT' && poll?.status === 'OPEN' && !poll.hasVoted;

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ScrollView contentContainerStyle={styles.content}>
          <Pressable style={({ pressed }) => [styles.secondaryButton, pressed && styles.pressed]} onPress={handleBack}>
            <ThemedText type="smallBold" style={styles.secondaryButtonText}>
              Back
            </ThemedText>
          </Pressable>

          {errorMessage && (
            <ThemedView type="backgroundElement" style={styles.messageBox}>
              <ThemedText type="small" style={styles.errorText}>
                {errorMessage}
              </ThemedText>
            </ThemedView>
          )}

          {poll && (
            <>
              <ThemedView style={styles.header}>
                <ThemedView style={[styles.badge, poll.status === 'OPEN' ? styles.openBadge : styles.closedBadge]}>
                  <ThemedText
                    type="smallBold"
                    style={poll.status === 'OPEN' ? styles.openBadgeText : styles.closedBadgeText}>
                    {poll.status}
                  </ThemedText>
                </ThemedView>
                <ThemedText type="subtitle" style={styles.title}>
                  {poll.title}
                </ThemedText>
                <ThemedText type="small" themeColor="textSecondary">
                  {formatDateTime(poll.startsAt)} - {formatDateTime(poll.endsAt)}
                </ThemedText>
              </ThemedView>

              <ThemedView type="backgroundElement" style={styles.card}>
                <ThemedText>{poll.description ?? 'No description was provided.'}</ThemedText>
                <ThemedText type="small" themeColor="textSecondary">
                  Your status: {poll.hasVoted ? 'Voted' : 'Not voted'}
                </ThemedText>
              </ThemedView>

              <ThemedView style={styles.options}>
                <ThemedText type="smallBold">Choose an option</ThemedText>
                {poll.options.map((option) => {
                  const selected = option.id === selectedOptionId;
                  return (
                    <Pressable
                      key={option.id}
                      disabled={!canVote}
                      style={({ pressed }) => [
                        styles.option,
                        { backgroundColor: selected ? '#2563eb' : '#ffffff', borderColor: selected ? '#2563eb' : '#e2e8f0' },
                        !canVote && styles.disabledOption,
                        pressed && styles.pressed,
                      ]}
                      onPress={() => setSelectedOptionId(option.id)}>
                      <ThemedText type="smallBold" style={selected ? styles.selectedText : undefined}>
                        {option.text}
                      </ThemedText>
                    </Pressable>
                  );
                })}
              </ThemedView>

              {role === 'ADMIN' && (
                <ThemedView type="backgroundElement" style={styles.messageBox}>
                  <ThemedText type="small">Administrators cannot participate in student polls.</ThemedText>
                </ThemedView>
              )}

              {role === 'STUDENT' && poll.hasVoted && (
                <ThemedView type="backgroundElement" style={styles.messageBox}>
                  <ThemedText type="small">You already voted in this poll.</ThemedText>
                </ThemedView>
              )}

              {canVote && (
                <Pressable
                  disabled={isSubmitting}
                  style={({ pressed }) => [
                    styles.primaryButton,
                    isSubmitting && styles.disabledButton,
                    pressed && !isSubmitting && styles.pressed,
                  ]}
                  onPress={handleVote}>
                  <ThemedText type="smallBold" style={styles.primaryButtonText}>
                    {isSubmitting ? 'Submitting...' : 'Vote'}
                  </ThemedText>
                </Pressable>
              )}

              <Pressable style={({ pressed }) => [styles.secondaryAction, pressed && styles.pressed]} onPress={handleLoadResult}>
                <ThemedText type="smallBold" style={styles.secondaryActionText}>
                  Check results
                </ThemedText>
              </Pressable>

              {resultMessage && (
                <ThemedView type="backgroundElement" style={styles.messageBox}>
                  <ThemedText type="small">{resultMessage}</ThemedText>
                </ThemedView>
              )}

              {result && (
                <ThemedView type="backgroundElement" style={styles.card}>
                  <ThemedText type="smallBold">Results</ThemedText>
                  <ThemedText type="small" themeColor="textSecondary">
                    Total votes: {result.totalVotes}
                  </ThemedText>
                  {result.options.map((option) => (
                    <ThemedView key={option.optionId} style={styles.resultRow}>
                      <ThemedView style={styles.resultLabelRow}>
                        <ThemedText type="smallBold">{option.text}</ThemedText>
                        <ThemedText type="small" themeColor="textSecondary">
                          {option.voteCount} votes, {option.percentage.toFixed(1)}%
                        </ThemedText>
                      </ThemedView>
                      <ThemedView style={styles.progressTrack}>
                        <ThemedView style={[styles.progressFill, { width: `${Math.min(option.percentage, 100)}%` }]} />
                      </ThemedView>
                    </ThemedView>
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
  header: {
    gap: Spacing.two,
  },
  title: {
    fontSize: 30,
    lineHeight: 38,
  },
  card: {
    borderRadius: Spacing.three,
    gap: Spacing.two,
    padding: Spacing.three,
  },
  badge: {
    alignSelf: 'flex-start',
    borderRadius: 999,
    paddingHorizontal: Spacing.two,
    paddingVertical: Spacing.one,
  },
  openBadge: {
    backgroundColor: '#dcfce7',
  },
  closedBadge: {
    backgroundColor: '#f1f5f9',
  },
  openBadgeText: {
    color: '#15803d',
  },
  closedBadgeText: {
    color: '#475569',
  },
  options: {
    gap: Spacing.two,
  },
  option: {
    borderRadius: Spacing.three,
    borderWidth: 1,
    padding: Spacing.three,
  },
  disabledOption: {
    opacity: 0.72,
  },
  selectedText: {
    color: '#ffffff',
  },
  primaryButton: {
    alignItems: 'center',
    backgroundColor: '#2563eb',
    borderRadius: Spacing.three,
    padding: Spacing.three,
  },
  primaryButtonText: {
    color: '#ffffff',
  },
  secondaryButton: {
    alignSelf: 'flex-start',
    backgroundColor: '#e0f2fe',
    borderRadius: Spacing.three,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
  },
  secondaryButtonText: {
    color: '#0369a1',
  },
  secondaryAction: {
    alignItems: 'center',
    backgroundColor: '#eef2ff',
    borderRadius: Spacing.three,
    padding: Spacing.three,
  },
  secondaryActionText: {
    color: '#3730a3',
  },
  disabledButton: {
    backgroundColor: '#94a3b8',
  },
  messageBox: {
    borderRadius: Spacing.three,
    padding: Spacing.three,
  },
  errorText: {
    color: '#dc2626',
  },
  resultRow: {
    backgroundColor: 'transparent',
    gap: Spacing.one,
  },
  resultLabelRow: {
    alignItems: 'center',
    backgroundColor: 'transparent',
    flexDirection: 'row',
    gap: Spacing.two,
    justifyContent: 'space-between',
  },
  progressTrack: {
    backgroundColor: '#e2e8f0',
    borderRadius: 999,
    height: 10,
    overflow: 'hidden',
  },
  progressFill: {
    backgroundColor: '#2563eb',
    borderRadius: 999,
    height: 10,
  },
  pressed: {
    opacity: 0.75,
  },
});
