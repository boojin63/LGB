import { useFocusEffect, useRouter } from 'expo-router';
import { useCallback, useState } from 'react';
import { FlatList, Platform, Pressable, RefreshControl, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { getPolls, PollListItem } from '@/api/polls';
import { ApiError } from '@/api/types';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useAuth } from '@/hooks/useAuth';

const PAGE_SIZE = 10;

export default function PollListScreen() {
  const router = useRouter();
  const { isAuthenticated, isLoading } = useAuth();
  const [polls, setPolls] = useState<PollListItem[]>([]);
  const [isInitialLoading, setIsInitialLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const loadPolls = useCallback(async () => {
    setErrorMessage(null);

    try {
      const response = await getPolls(0, PAGE_SIZE);
      setPolls(response.content);
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage('Polls could not be loaded. Check the backend and try again.');
      }
    } finally {
      setIsInitialLoading(false);
      setIsRefreshing(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      if (isLoading || !isAuthenticated) {
        return;
      }

      void loadPolls();
    }, [isAuthenticated, isLoading, loadPolls]),
  );

  async function handleRefresh() {
    setIsRefreshing(true);
    await loadPolls();
  }

  function handleOpenPoll(pollId: number) {
    if (Platform.OS === 'web') {
      window.location.assign(`/polls/${pollId}`);
      return;
    }

    router.push({
      pathname: '/polls/[pollId]',
      params: { pollId: String(pollId) },
    } as never);
  }

  function renderPoll({ item }: { item: PollListItem }) {
    const open = item.status === 'OPEN';

    return (
      <Pressable style={({ pressed }) => [styles.card, pressed && styles.pressed]} onPress={() => handleOpenPoll(item.id)}>
        <ThemedView style={styles.cardHeader}>
          <ThemedView style={[styles.badge, open ? styles.openBadge : styles.closedBadge]}>
            <ThemedText type="smallBold" style={open ? styles.openBadgeText : styles.closedBadgeText}>
              {item.status}
            </ThemedText>
          </ThemedView>
          <ThemedText type="smallBold" style={item.hasVoted ? styles.votedText : styles.notVotedText}>
            {item.hasVoted ? 'Voted' : 'Not voted'}
          </ThemedText>
        </ThemedView>
        <ThemedText type="smallBold" style={styles.pollTitle}>
          {item.title}
        </ThemedText>
        <ThemedText type="small" themeColor="textSecondary">
          {formatDateTime(item.startsAt)} - {formatDateTime(item.endsAt)}
        </ThemedText>
        <ThemedText type="small" themeColor="textSecondary">
          Results: {item.resultVisible ? 'Available when allowed' : 'Not public yet'}
        </ThemedText>
      </Pressable>
    );
  }

  if (isLoading || isInitialLoading) {
    return (
      <ThemedView style={styles.container}>
        <SafeAreaView style={styles.safeArea}>
          <ThemedText type="subtitle">Loading polls...</ThemedText>
        </SafeAreaView>
      </ThemedView>
    );
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedView style={styles.header}>
          <ThemedText type="subtitle" style={styles.title}>
            Polls
          </ThemedText>
          <ThemedText type="small" themeColor="textSecondary">
            Join active student polls and check results when they are visible.
          </ThemedText>
        </ThemedView>

        {errorMessage && (
          <ThemedView type="backgroundElement" style={styles.messageBox}>
            <ThemedText type="small" style={styles.errorText}>
              {errorMessage}
            </ThemedText>
          </ThemedView>
        )}

        <FlatList
          data={polls}
          keyExtractor={(item) => String(item.id)}
          renderItem={renderPoll}
          contentContainerStyle={styles.listContent}
          refreshControl={<RefreshControl refreshing={isRefreshing} onRefresh={handleRefresh} />}
          ListEmptyComponent={
            <ThemedView type="backgroundElement" style={styles.messageBox}>
              <ThemedText type="small">No active or recent polls are available.</ThemedText>
            </ThemedView>
          }
        />
      </SafeAreaView>
    </ThemedView>
  );
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
    paddingHorizontal: Spacing.four,
  },
  header: {
    gap: Spacing.one,
    paddingBottom: Spacing.three,
    paddingTop: Spacing.four,
  },
  title: {
    fontSize: 30,
    lineHeight: 38,
  },
  listContent: {
    gap: Spacing.three,
    paddingBottom: Spacing.four,
  },
  card: {
    backgroundColor: '#ffffff',
    borderColor: '#e2e8f0',
    borderRadius: Spacing.three,
    borderWidth: 1,
    gap: Spacing.two,
    padding: Spacing.three,
  },
  cardHeader: {
    alignItems: 'center',
    backgroundColor: 'transparent',
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  badge: {
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
  votedText: {
    color: '#0f766e',
  },
  notVotedText: {
    color: '#ea580c',
  },
  pollTitle: {
    fontSize: 17,
    lineHeight: 24,
  },
  messageBox: {
    borderRadius: Spacing.three,
    marginBottom: Spacing.three,
    padding: Spacing.three,
  },
  errorText: {
    color: '#dc2626',
  },
  pressed: {
    opacity: 0.75,
  },
});
