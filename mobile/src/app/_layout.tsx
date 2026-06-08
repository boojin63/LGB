import { DarkTheme, DefaultTheme, Redirect, Slot, ThemeProvider, usePathname } from 'expo-router';
import { StyleSheet, useColorScheme } from 'react-native';

import { AnimatedSplashOverlay } from '@/components/animated-icon';
import AppTabs from '@/components/app-tabs';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { AuthProvider } from '@/context/AuthContext';
import { useAuth } from '@/hooks/useAuth';

export default function TabLayout() {
  const colorScheme = useColorScheme();

  return (
    <ThemeProvider value={colorScheme === 'dark' ? DarkTheme : DefaultTheme}>
      <AuthProvider>
        <AnimatedSplashOverlay />
        <AuthGate />
      </AuthProvider>
    </ThemeProvider>
  );
}

function AuthGate() {
  const pathname = usePathname();
  const { isLoading, isAuthenticated } = useAuth();
  const usesTabLayout = pathname === '/' || pathname === '/explore';
  const isPublicRoute = pathname === '/login';

  if (isLoading) {
    return <LoadingScreen />;
  }

  if (!isAuthenticated && !isPublicRoute) {
    return <Redirect href="/login" />;
  }

  if (isAuthenticated && isPublicRoute) {
    return <Redirect href="/" />;
  }

  return usesTabLayout ? <AppTabs /> : <Slot />;
}

function LoadingScreen() {
  return (
    <ThemedView style={styles.loadingContainer}>
      <ThemedText type="subtitle">세션 확인 중...</ThemedText>
    </ThemedView>
  );
}

const styles = StyleSheet.create({
  loadingContainer: {
    alignItems: 'center',
    flex: 1,
    justifyContent: 'center',
  },
});
