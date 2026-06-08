import { useRouter } from 'expo-router';
import { useEffect, useState } from 'react';
import { Pressable, StyleSheet, TextInput } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ApiError } from '@/api/types';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useAuth } from '@/hooks/useAuth';

export default function LoginScreen() {
  const router = useRouter();
  const theme = useTheme();
  const { login, isAuthenticated, isLoading } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (!isLoading && isAuthenticated) {
      router.replace('/');
    }
  }, [isAuthenticated, isLoading, router]);

  async function handleLogin() {
    const trimmedEmail = email.trim();

    if (!trimmedEmail || !password) {
      setErrorMessage('이메일과 비밀번호를 입력하세요.');
      return;
    }

    setIsSubmitting(true);
    setErrorMessage(null);

    try {
      await login(trimmedEmail, password);
      router.replace('/');
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage('로그인 중 오류가 발생했습니다.');
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedView style={styles.content}>
          <ThemedText type="title" style={styles.title}>
            LGB Project
          </ThemedText>
          <ThemedText themeColor="textSecondary" style={styles.description}>
            소프트웨어융합학과 통합 정보 관리 앱
          </ThemedText>

          <ThemedView type="backgroundElement" style={styles.form}>
            <ThemedText type="smallBold">이메일</ThemedText>
            <TextInput
              autoCapitalize="none"
              autoCorrect={false}
              keyboardType="email-address"
              placeholder="email@example.com"
              placeholderTextColor={theme.textSecondary}
              style={[
                styles.input,
                {
                  borderColor: theme.backgroundSelected,
                  color: theme.text,
                  backgroundColor: theme.background,
                },
              ]}
              value={email}
              onChangeText={setEmail}
            />

            <ThemedText type="smallBold">비밀번호</ThemedText>
            <TextInput
              placeholder="비밀번호"
              placeholderTextColor={theme.textSecondary}
              secureTextEntry
              style={[
                styles.input,
                {
                  borderColor: theme.backgroundSelected,
                  color: theme.text,
                  backgroundColor: theme.background,
                },
              ]}
              value={password}
              onChangeText={setPassword}
            />

            {errorMessage && (
              <ThemedText type="small" style={styles.errorText}>
                {errorMessage}
              </ThemedText>
            )}

            <Pressable
              disabled={isSubmitting}
              style={({ pressed }) => [
                styles.button,
                (pressed || isSubmitting) && styles.buttonDisabled,
              ]}
              onPress={handleLogin}>
              <ThemedText type="smallBold" style={styles.buttonText}>
                {isSubmitting ? '로그인 중...' : '로그인'}
              </ThemedText>
            </Pressable>
          </ThemedView>

          <ThemedView type="backgroundElement" style={styles.seedBox}>
            <ThemedText type="smallBold">개발용 계정 이메일</ThemedText>
            <ThemedText type="small">ADMIN: admin@lgb.local</ThemedText>
            <ThemedText type="small">STUDENT: student@lgb.local</ThemedText>
            <ThemedText type="small" themeColor="textSecondary">
              비밀번호는 개발 seed 설정을 확인하세요.
            </ThemedText>
          </ThemedView>
        </ThemedView>
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
    paddingHorizontal: Spacing.four,
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    gap: Spacing.three,
  },
  title: {
    textAlign: 'center',
  },
  description: {
    textAlign: 'center',
  },
  form: {
    gap: Spacing.two,
    borderRadius: Spacing.four,
    padding: Spacing.four,
  },
  input: {
    borderRadius: Spacing.two,
    borderWidth: 1,
    fontSize: 16,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.three,
  },
  button: {
    alignItems: 'center',
    backgroundColor: '#2563eb',
    borderRadius: Spacing.three,
    marginTop: Spacing.two,
    paddingVertical: Spacing.three,
  },
  buttonDisabled: {
    opacity: 0.7,
  },
  buttonText: {
    color: '#ffffff',
  },
  errorText: {
    color: '#dc2626',
  },
  seedBox: {
    borderRadius: Spacing.four,
    gap: Spacing.one,
    padding: Spacing.three,
  },
});
