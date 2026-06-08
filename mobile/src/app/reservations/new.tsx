import { useFocusEffect, useRouter } from 'expo-router';
import { useCallback, useState } from 'react';
import { Button, FlatList, Platform, Pressable, StyleSheet, TextInput } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { createReservation } from '@/api/reservations';
import { getResources, ResourceItem } from '@/api/resources';
import { ApiError } from '@/api/types';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useAuth } from '@/hooks/useAuth';

export default function NewReservationScreen() {
  const router = useRouter();
  const theme = useTheme();
  const { isAuthenticated, isLoading } = useAuth();
  const [resources, setResources] = useState<ResourceItem[]>([]);
  const [selectedResourceId, setSelectedResourceId] = useState<number | null>(null);
  const [startAt, setStartAt] = useState('');
  const [endAt, setEndAt] = useState('');
  const [purpose, setPurpose] = useState('');
  const [isInitialLoading, setIsInitialLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const loadResources = useCallback(async () => {
    setErrorMessage(null);

    try {
      const response = await getResources();
      setResources(response);
      setSelectedResourceId((current) => current ?? response[0]?.id ?? null);
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage('Failed to load resources.');
      }
    } finally {
      setIsInitialLoading(false);
    }
  }, []);

  useFocusEffect(
    useCallback(() => {
      if (isLoading || !isAuthenticated) {
        return;
      }

      void loadResources();
    }, [isAuthenticated, isLoading, loadResources]),
  );

  async function handleSubmit() {
    if (!selectedResourceId) {
      setErrorMessage('Select a resource.');
      return;
    }

    if (!startAt.trim() || !endAt.trim() || !purpose.trim()) {
      setErrorMessage('Enter start time, end time, and purpose.');
      return;
    }

    setIsSubmitting(true);
    setErrorMessage(null);

    try {
      await createReservation({
        resourceId: selectedResourceId,
        startAt: startAt.trim(),
        endAt: endAt.trim(),
        purpose: purpose.trim(),
      });

      if (Platform.OS === 'web') {
        window.location.assign('/reservations');
        return;
      }

      router.replace('/reservations/index' as never);
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage('Failed to create reservation.');
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  function handleBack() {
    if (router.canGoBack()) {
      router.back();
      return;
    }

    if (Platform.OS === 'web') {
      window.location.assign('/reservations');
      return;
    }

    router.replace('/reservations/index' as never);
  }

  function renderResource({ item }: { item: ResourceItem }) {
    const selected = item.id === selectedResourceId;

    return (
      <Pressable
        style={({ pressed }) => [
          styles.resourceOption,
          { backgroundColor: selected ? '#2563eb' : theme.backgroundElement },
          pressed && styles.pressed,
        ]}
        onPress={() => setSelectedResourceId(item.id)}>
        <ThemedText type="smallBold" style={selected ? styles.selectedText : undefined}>
          {item.name}
        </ThemedText>
        <ThemedText type="small" style={selected ? styles.selectedText : undefined}>
          {item.type} - {item.location ?? 'No location'}
        </ThemedText>
      </Pressable>
    );
  }

  if (isLoading || isInitialLoading) {
    return (
      <ThemedView style={styles.container}>
        <SafeAreaView style={styles.safeArea}>
          <ThemedText type="subtitle">Loading reservation form...</ThemedText>
        </SafeAreaView>
      </ThemedView>
    );
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <FlatList
          data={resources}
          keyExtractor={(item) => String(item.id)}
          renderItem={renderResource}
          contentContainerStyle={styles.content}
          ListHeaderComponent={
            <ThemedView style={styles.section}>
              <ThemedView style={styles.buttonWrap}>
                <Button title="Back" onPress={handleBack} />
              </ThemedView>
              <ThemedText type="subtitle">New Reservation</ThemedText>
              <ThemedText type="small" themeColor="textSecondary">
                Time format example: 2026-12-10T10:00:00
              </ThemedText>

              {errorMessage && (
                <ThemedView type="backgroundElement" style={styles.messageBox}>
                  <ThemedText type="small" style={styles.errorText}>
                    {errorMessage}
                  </ThemedText>
                </ThemedView>
              )}

              <ThemedText type="smallBold">Resource</ThemedText>
            </ThemedView>
          }
          ListFooterComponent={
            <ThemedView style={styles.section}>
              <TextInput
                value={startAt}
                onChangeText={setStartAt}
                placeholder="Start at"
                autoCapitalize="none"
                style={[styles.input, { color: theme.text, borderColor: theme.textSecondary }]}
              />
              <TextInput
                value={endAt}
                onChangeText={setEndAt}
                placeholder="End at"
                autoCapitalize="none"
                style={[styles.input, { color: theme.text, borderColor: theme.textSecondary }]}
              />
              <TextInput
                value={purpose}
                onChangeText={setPurpose}
                placeholder="Purpose"
                multiline
                style={[
                  styles.input,
                  styles.purposeInput,
                  { color: theme.text, borderColor: theme.textSecondary },
                ]}
              />
              <ThemedView style={styles.buttonWrap}>
                <Button
                  title={isSubmitting ? 'Submitting...' : 'Submit reservation'}
                  onPress={handleSubmit}
                  disabled={isSubmitting}
                />
              </ThemedView>
            </ThemedView>
          }
          ListEmptyComponent={
            <ThemedView type="backgroundElement" style={styles.messageBox}>
              <ThemedText type="small">No resources are available.</ThemedText>
            </ThemedView>
          }
        />
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
    paddingHorizontal: Spacing.four,
  },
  content: {
    gap: Spacing.three,
    paddingBottom: Spacing.four,
    paddingTop: Spacing.four,
  },
  section: {
    gap: Spacing.three,
  },
  resourceOption: {
    borderRadius: Spacing.three,
    gap: Spacing.one,
    padding: Spacing.three,
  },
  selectedText: {
    color: '#ffffff',
  },
  input: {
    borderRadius: Spacing.two,
    borderWidth: 1,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
  },
  purposeInput: {
    minHeight: 88,
    textAlignVertical: 'top',
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
