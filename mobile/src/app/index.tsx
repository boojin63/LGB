import { useRouter } from 'expo-router';
import { Platform, Pressable, ScrollView, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useAuth } from '@/hooks/useAuth';

type RouteTarget = {
  title: string;
  description: string;
  meta: string;
  webPath: string;
  nativePath: string;
};

const MAIN_FEATURES: RouteTarget[] = [
  {
    title: 'Chat',
    description: 'Talk with department members in a simple mobile chat room.',
    meta: 'Fast communication',
    webPath: '/chat',
    nativePath: '/chat/index',
  },
  {
    title: 'LGB AI Chatbot',
    description: 'Ask about notices, calendar events, reservations, and polls.',
    meta: 'Campus guide',
    webPath: '/chatbot',
    nativePath: '/chatbot/index',
  },
  {
    title: 'Polls',
    description: 'Join active department polls and check available results.',
    meta: 'Student participation',
    webPath: '/polls',
    nativePath: '/polls/index',
  },
];

const SUPPORT_FEATURES: RouteTarget[] = [
  {
    title: 'Notices',
    description: 'Department announcements',
    meta: 'Read updates',
    webPath: '/notices',
    nativePath: '/notices',
  },
  {
    title: 'Calendar',
    description: 'Important schedules',
    meta: 'View dates',
    webPath: '/calendar',
    nativePath: '/calendar/index',
  },
  {
    title: 'Reservations',
    description: 'My reservation requests',
    meta: 'Request and review',
    webPath: '/reservations',
    nativePath: '/reservations/index',
  },
  {
    title: 'Resources',
    description: 'Reservable rooms and equipment',
    meta: 'Check availability',
    webPath: '/resources',
    nativePath: '/resources/index',
  },
];

export default function HomeScreen() {
  const router = useRouter();
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

  function renderMainCard(target: RouteTarget, index: number) {
    return (
      <Pressable
        key={target.webPath}
        style={({ pressed }) => [
          styles.mainCard,
          index === 1 && styles.mainCardAlt,
          pressed && styles.pressed,
        ]}
        onPress={() => openRoute(target)}>
        <ThemedView style={styles.cardTopLine}>
          <ThemedText type="smallBold" style={styles.mainMeta}>
            {target.meta}
          </ThemedText>
          <ThemedText type="smallBold" style={styles.mainArrow}>
            Open
          </ThemedText>
        </ThemedView>
        <ThemedText type="subtitle" style={styles.mainTitle}>
          {target.title}
        </ThemedText>
        <ThemedText type="small" style={styles.mainDescription}>
          {target.description}
        </ThemedText>
      </Pressable>
    );
  }

  function renderSupportCard(target: RouteTarget) {
    return (
      <Pressable
        key={target.webPath}
        style={({ pressed }) => [styles.supportCard, pressed && styles.pressed]}
        onPress={() => openRoute(target)}>
        <ThemedText type="smallBold">{target.title}</ThemedText>
        <ThemedText type="small" themeColor="textSecondary">
          {target.description}
        </ThemedText>
        <ThemedText type="smallBold" style={styles.supportMeta}>
          {target.meta}
        </ThemedText>
      </Pressable>
    );
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ScrollView contentContainerStyle={styles.content}>
          <ThemedView style={styles.header}>
            <ThemedView>
              <ThemedText type="title" style={styles.title}>
                LGB Project
              </ThemedText>
              <ThemedText type="small" themeColor="textSecondary">
                {user?.name ?? 'User'}
              </ThemedText>
            </ThemedView>
            <ThemedView style={styles.roleBadge}>
              <ThemedText type="smallBold" style={styles.roleText}>
                {role ?? 'ROLE'}
              </ThemedText>
            </ThemedView>
          </ThemedView>

          <ThemedView style={styles.section}>
            <ThemedText type="smallBold" style={styles.sectionLabel}>
              Core mobile demo
            </ThemedText>
            {MAIN_FEATURES.map(renderMainCard)}
          </ThemedView>

          <ThemedView style={styles.section}>
            <ThemedText type="smallBold" style={styles.sectionLabel}>
              Supporting features
            </ThemedText>
            <ThemedView style={styles.supportGrid}>{SUPPORT_FEATURES.map(renderSupportCard)}</ThemedView>
          </ThemedView>

          <Pressable style={({ pressed }) => [styles.logoutButton, pressed && styles.pressed]} onPress={handleLogout}>
            <ThemedText type="smallBold" style={styles.logoutText}>
              Log out
            </ThemedText>
          </Pressable>
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
    alignItems: 'flex-start',
    flexDirection: 'row',
    gap: Spacing.three,
    justifyContent: 'space-between',
  },
  title: {
    fontSize: 38,
    lineHeight: 42,
  },
  roleBadge: {
    backgroundColor: '#dbeafe',
    borderColor: '#93c5fd',
    borderRadius: 999,
    borderWidth: 1,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
  },
  roleText: {
    color: '#1d4ed8',
  },
  section: {
    gap: Spacing.three,
  },
  sectionLabel: {
    color: '#334155',
  },
  mainCard: {
    backgroundColor: '#1d4ed8',
    borderRadius: Spacing.three,
    gap: Spacing.two,
    minHeight: 150,
    padding: Spacing.four,
  },
  mainCardAlt: {
    backgroundColor: '#0f766e',
  },
  cardTopLine: {
    alignItems: 'center',
    backgroundColor: 'transparent',
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  mainMeta: {
    color: '#bfdbfe',
  },
  mainArrow: {
    color: '#ffffff',
  },
  mainTitle: {
    color: '#ffffff',
    fontSize: 26,
    lineHeight: 32,
  },
  mainDescription: {
    color: '#eff6ff',
  },
  supportGrid: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: Spacing.three,
  },
  supportCard: {
    backgroundColor: '#ffffff',
    borderColor: '#e2e8f0',
    borderRadius: Spacing.three,
    borderWidth: 1,
    flexBasis: '47%',
    flexGrow: 1,
    gap: Spacing.one,
    minHeight: 112,
    padding: Spacing.three,
  },
  supportMeta: {
    color: '#2563eb',
    marginTop: 'auto',
  },
  logoutButton: {
    alignItems: 'center',
    backgroundColor: '#fee2e2',
    borderRadius: Spacing.three,
    padding: Spacing.three,
  },
  logoutText: {
    color: '#b91c1c',
  },
  pressed: {
    opacity: 0.78,
  },
});
