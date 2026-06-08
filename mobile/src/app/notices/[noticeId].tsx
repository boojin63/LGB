import { useFocusEffect, useLocalSearchParams, useRouter } from 'expo-router';
import { useCallback, useState } from 'react';
import { Pressable, ScrollView, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { getNotice, NoticeDetail } from '@/api/notices';
import { ApiError } from '@/api/types';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useAuth } from '@/hooks/useAuth';

export default function NoticeDetailScreen() {
  const router = useRouter();
  const theme = useTheme();
  const params = useLocalSearchParams<{ noticeId?: string | string[] }>();
  const { isAuthenticated, isLoading } = useAuth();
  const [notice, setNotice] = useState<NoticeDetail | null>(null);
  const [isNoticeLoading, setIsNoticeLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const noticeId = parseNoticeId(params.noticeId);

  const loadNotice = useCallback(async () => {
    if (!noticeId) {
      setNotice(null);
      setErrorMessage('잘못된 공지 ID입니다.');
      setIsNoticeLoading(false);
      return;
    }

    setIsNoticeLoading(true);
    setErrorMessage(null);

    try {
      const response = await getNotice(noticeId);
      setNotice(response);
    } catch (error) {
      setNotice(null);

      if (error instanceof ApiError) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage('공지 상세를 불러오지 못했습니다.');
      }
    } finally {
      setIsNoticeLoading(false);
    }
  }, [noticeId]);

  useFocusEffect(
    useCallback(() => {
      if (isLoading) {
        return;
      }

      if (!isAuthenticated) {
        router.replace('/login');
        return;
      }

      void loadNotice();
    }, [isAuthenticated, isLoading, loadNotice, router]),
  );

  function handleBack() {
    if (router.canGoBack()) {
      router.back();
      return;
    }

    router.replace('/notices');
  }

  if (isLoading || isNoticeLoading) {
    return (
      <ThemedView style={styles.container}>
        <SafeAreaView style={styles.safeArea}>
          <ThemedText type="subtitle">공지 상세 로딩 중...</ThemedText>
        </SafeAreaView>
      </ThemedView>
    );
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ScrollView contentContainerStyle={styles.scrollContent}>
          <Pressable
            style={({ pressed }) => [
              styles.backButton,
              { backgroundColor: theme.backgroundElement },
              pressed && styles.pressed,
            ]}
            onPress={handleBack}>
            <ThemedText type="smallBold">뒤로가기</ThemedText>
          </Pressable>

          {errorMessage && (
            <ThemedView type="backgroundElement" style={styles.messageBox}>
              <ThemedText type="small" style={styles.errorText}>
                {errorMessage}
              </ThemedText>
            </ThemedView>
          )}

          {notice && (
            <ThemedView style={styles.content}>
              {notice.pinned && (
                <ThemedText type="smallBold" style={styles.pinned}>
                  PINNED
                </ThemedText>
              )}

              <ThemedText type="subtitle">{notice.title}</ThemedText>

              <ThemedView type="backgroundElement" style={styles.metaBox}>
                <ThemedText type="small">작성자: {notice.author.name}</ThemedText>
                <ThemedText type="small">조회수: {notice.viewCount}</ThemedText>
                <ThemedText type="small">
                  작성일: {formatDateTime(notice.createdAt)}
                </ThemedText>
                <ThemedText type="small">
                  수정일: {formatDateTime(notice.updatedAt)}
                </ThemedText>
              </ThemedView>

              <ThemedView type="backgroundElement" style={styles.bodyBox}>
                <ThemedText>{notice.content}</ThemedText>
              </ThemedView>
            </ThemedView>
          )}
        </ScrollView>
      </SafeAreaView>
    </ThemedView>
  );
}

function parseNoticeId(value: string | string[] | undefined): number | null {
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
  scrollContent: {
    gap: Spacing.three,
    padding: Spacing.four,
  },
  backButton: {
    alignSelf: 'flex-start',
    borderRadius: Spacing.three,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
  },
  content: {
    gap: Spacing.three,
  },
  pinned: {
    color: '#2563eb',
  },
  metaBox: {
    borderRadius: Spacing.three,
    gap: Spacing.one,
    padding: Spacing.three,
  },
  bodyBox: {
    borderRadius: Spacing.three,
    padding: Spacing.three,
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
