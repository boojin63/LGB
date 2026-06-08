import { useRouter } from 'expo-router';
import { Button, Platform, Pressable, ScrollView, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useAuth } from '@/hooks/useAuth';

type RouteTarget = {
  title: string;
  description: string;
  webPath: string;
  nativePath: string;
};

const MAIN_FEATURES: RouteTarget[] = [
  {
    title: '채팅',
    description: '학과 공지방과 대화 흐름을 확인합니다.',
    webPath: '/chat',
    nativePath: '/chat/index',
  },
  {
    title: 'AI 챗봇',
    description: '공지, 일정, 예약, 투표 도움말을 질문합니다.',
    webPath: '/chatbot',
    nativePath: '/chatbot/index',
  },
  {
    title: '투표',
    description: '진행 중인 투표에 참여하고 결과를 확인합니다.',
    webPath: '/polls',
    nativePath: '/polls/index',
  },
];

const SUPPORT_FEATURES: RouteTarget[] = [
  {
    title: '공지',
    description: '학과 공지사항',
    webPath: '/notices',
    nativePath: '/notices',
  },
  {
    title: '일정',
    description: '학과 일정',
    webPath: '/calendar',
    nativePath: '/calendar/index',
  },
  {
    title: '예약',
    description: '내 예약 확인',
    webPath: '/reservations',
    nativePath: '/reservations/index',
  },
  {
    title: '자원',
    description: '예약 가능 자원',
    webPath: '/resources',
    nativePath: '/resources/index',
  },
];

export default function HomeScreen() {
  const router = useRouter();
  const theme = useTheme();
  const { user, role, logout } = useAuth();

  async function handleLogout() {
    await logout();
    router.replace('/login');
  }

  function openRoute(target: RouteTarget) {
    if (Platform.OS === 'web') {
      window.location.assign(target.webPath);
      return;
    }

    router.push(target.nativePath as never);
  }

  function renderFeatureCard(target: RouteTarget, primary: boolean) {
    return (
      <Pressable
        key={target.webPath}
        style={({ pressed }) => [
          primary ? styles.mainCard : styles.supportCard,
          { backgroundColor: primary ? '#2563eb' : theme.backgroundElement },
          pressed && styles.pressed,
        ]}
        onPress={() => openRoute(target)}>
        <ThemedText type="smallBold" style={primary ? styles.mainCardText : undefined}>
          {target.title}
        </ThemedText>
        <ThemedText type="small" style={primary ? styles.mainCardText : undefined}>
          {target.description}
        </ThemedText>
      </Pressable>
    );
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ScrollView contentContainerStyle={styles.content}>
          <ThemedView style={styles.header}>
            <ThemedText type="title">LGB Project</ThemedText>
            <ThemedText type="small" themeColor="textSecondary">
              {user?.name ?? '사용자'} · {role ?? 'ROLE'}
            </ThemedText>
          </ThemedView>

          <ThemedView style={styles.section}>
            <ThemedText type="subtitle">내일 시연 핵심 기능</ThemedText>
            {MAIN_FEATURES.map((feature) => renderFeatureCard(feature, true))}
          </ThemedView>

          <ThemedView style={styles.section}>
            <ThemedText type="subtitle">보조 기능</ThemedText>
            <ThemedView style={styles.supportGrid}>
              {SUPPORT_FEATURES.map((feature) => renderFeatureCard(feature, false))}
            </ThemedView>
          </ThemedView>

          <ThemedView style={styles.logoutWrap}>
            <Button title="로그아웃" onPress={handleLogout} />
          </ThemedView>
        </ScrollView>
      </SafeAreaView>
    </ThemedView>
  );
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
    gap: Spacing.four,
    padding: Spacing.four,
  },
  header: {
    gap: Spacing.one,
  },
  section: {
    gap: Spacing.three,
  },
  mainCard: {
    borderRadius: Spacing.three,
    gap: Spacing.one,
    padding: Spacing.four,
  },
  mainCardText: {
    color: '#ffffff',
  },
  supportGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Spacing.three,
  },
  supportCard: {
    borderRadius: Spacing.three,
    flexBasis: '47%',
    flexGrow: 1,
    gap: Spacing.one,
    minHeight: 92,
    padding: Spacing.three,
  },
  logoutWrap: {
    borderRadius: Spacing.three,
    overflow: 'hidden',
  },
  pressed: {
    opacity: 0.75,
  },
});
