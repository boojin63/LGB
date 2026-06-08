import { useFocusEffect, useRouter } from 'expo-router';
import { useCallback, useState } from 'react';
import { FlatList, Pressable, RefreshControl, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { getNotices, NoticeListItem } from '@/api/notices';
import { ApiError } from '@/api/types';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useAuth } from '@/hooks/useAuth';

const PAGE_SIZE = 10;

export default function NoticeListScreen() {
  const router = useRouter();
  const theme = useTheme();
  const { isAuthenticated, isLoading } = useAuth();
  const [notices, setNotices] = useState<NoticeListItem[]>([]);
  const [page, setPage] = useState(0);
  const [last, setLast] = useState(true);
  const [isInitialLoading, setIsInitialLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [isLoadingMore, setIsLoadingMore] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const loadNotices = useCallback(async (targetPage: number, replace: boolean) => {
    if (replace) {
      setIsInitialLoading(targetPage === 0);
    } else {
      setIsLoadingMore(true);
    }

    setErrorMessage(null);

    try {
      const response = await getNotices(targetPage, PAGE_SIZE);
      setNotices((current) => (replace ? response.content : [...current, ...response.content]));
      setPage(response.page);
      setLast(response.last);
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage('공지 목록을 불러오지 못했습니다.');
      }
    } finally {
      setIsInitialLoading(false);
      setIsRefreshing(false);
      setIsLoadingMore(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      if (isLoading) {
        return;
      }

      if (!isAuthenticated) {
        router.replace('/login');
        return;
      }

      void loadNotices(0, true);
    }, [isAuthenticated, isLoading, loadNotices, router]),
  );

  async function handleRefresh() {
    setIsRefreshing(true);
    await loadNotices(0, true);
  }

  function handleLoadMore() {
    if (last || isLoadingMore || isInitialLoading) {
      return;
    }

    void loadNotices(page + 1, false);
  }

  function handleOpenNotice(noticeId: number) {
    router.push({
      pathname: '/notices/[noticeId]',
      params: { noticeId: String(noticeId) },
    });
  }

  function renderNotice({ item }: { item: NoticeListItem }) {
    return (
      <Pressable
        style={({ pressed }) => [
          styles.noticeCard,
          { backgroundColor: theme.backgroundElement },
          pressed && styles.pressed,
        ]}
        onPress={() => handleOpenNotice(item.id)}>
        <ThemedView type="backgroundElement" style={styles.noticeContent}>
          <ThemedView type="backgroundElement" style={styles.noticeHeader}>
            {item.pinned && (
              <ThemedText type="smallBold" style={styles.pinned}>
                PINNED
              </ThemedText>
            )}
            <ThemedText type="small" themeColor="textSecondary">
              조회 {item.viewCount}
            </ThemedText>
          </ThemedView>
          <ThemedText type="smallBold">{item.title}</ThemedText>
          <ThemedText type="small" themeColor="textSecondary">
            {item.authorName} · {formatDateTime(item.createdAt)}
          </ThemedText>
        </ThemedView>
      </Pressable>
    );
  }

  if (isLoading || isInitialLoading) {
    return (
      <ThemedView style={styles.container}>
        <SafeAreaView style={styles.safeArea}>
          <ThemedText type="subtitle">공지 목록 로딩 중...</ThemedText>
        </SafeAreaView>
      </ThemedView>
    );
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedView style={styles.header}>
          <ThemedText type="subtitle">공지사항</ThemedText>
          <ThemedText type="small" themeColor="textSecondary">
            학과 공지 목록입니다.
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
          data={notices}
          keyExtractor={(item) => String(item.id)}
          renderItem={renderNotice}
          contentContainerStyle={styles.listContent}
          refreshControl={
            <RefreshControl refreshing={isRefreshing} onRefresh={handleRefresh} />
          }
          ListEmptyComponent={
            <ThemedView type="backgroundElement" style={styles.messageBox}>
              <ThemedText type="small">등록된 공지가 없습니다.</ThemedText>
            </ThemedView>
          }
          ListFooterComponent={
            isLoadingMore ? (
              <ThemedText type="small" style={styles.footerText}>
                더 불러오는 중...
              </ThemedText>
            ) : null
          }
          onEndReached={handleLoadMore}
          onEndReachedThreshold={0.4}
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
  listContent: {
    gap: Spacing.three,
    paddingBottom: Spacing.four,
  },
  noticeCard: {
    borderRadius: Spacing.three,
  },
  noticeContent: {
    borderRadius: Spacing.three,
    gap: Spacing.two,
    padding: Spacing.three,
  },
  noticeHeader: {
    alignItems: 'center',
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  pinned: {
    color: '#2563eb',
  },
  messageBox: {
    borderRadius: Spacing.three,
    marginBottom: Spacing.three,
    padding: Spacing.three,
  },
  errorText: {
    color: '#dc2626',
  },
  footerText: {
    paddingVertical: Spacing.three,
    textAlign: 'center',
  },
  pressed: {
    opacity: 0.75,
  },
});
