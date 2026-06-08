import { useFocusEffect, useLocalSearchParams, useRouter } from 'expo-router';
import { useCallback, useState } from 'react';
import { Platform, Pressable, ScrollView, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { CalendarEventDetail, getCalendarEvent } from '@/api/calendar';
import { ApiError } from '@/api/types';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useAuth } from '@/hooks/useAuth';

export default function CalendarDetailScreen() {
  const router = useRouter();
  const theme = useTheme();
  const params = useLocalSearchParams<{ eventId?: string | string[] }>();
  const { isAuthenticated, isLoading } = useAuth();
  const [event, setEvent] = useState<CalendarEventDetail | null>(null);
  const [isEventLoading, setIsEventLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const eventId = parseEventId(params.eventId);

  const loadEvent = useCallback(async () => {
    if (!eventId) {
      setEvent(null);
      setErrorMessage('Invalid calendar event ID.');
      setIsEventLoading(false);
      return;
    }

    setIsEventLoading(true);
    setErrorMessage(null);

    try {
      const response = await getCalendarEvent(eventId);
      setEvent(response);
    } catch (error) {
      setEvent(null);

      if (error instanceof ApiError) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage('Failed to load calendar event detail.');
      }
    } finally {
      setIsEventLoading(false);
    }
  }, [eventId]);

  useFocusEffect(
    useCallback(() => {
      if (isLoading) {
        return;
      }

      if (!isAuthenticated) {
        router.replace('/login');
        return;
      }

      void loadEvent();
    }, [isAuthenticated, isLoading, loadEvent, router]),
  );

  function handleBack() {
    if (router.canGoBack()) {
      router.back();
      return;
    }

    if (Platform.OS === 'web') {
      window.location.assign('/calendar');
      return;
    }

    router.replace('/calendar/index');
  }

  if (isLoading || isEventLoading) {
    return (
      <ThemedView style={styles.container}>
        <SafeAreaView style={styles.safeArea}>
          <ThemedText type="subtitle">Loading calendar event...</ThemedText>
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
            <ThemedText type="smallBold">Back</ThemedText>
          </Pressable>

          {errorMessage && (
            <ThemedView type="backgroundElement" style={styles.messageBox}>
              <ThemedText type="small" style={styles.errorText}>
                {errorMessage}
              </ThemedText>
            </ThemedView>
          )}

          {event && (
            <ThemedView style={styles.content}>
              <ThemedText type="subtitle">{event.title}</ThemedText>

              <ThemedView type="backgroundElement" style={styles.metaBox}>
                <ThemedText type="small">Location: {event.location ?? 'No location'}</ThemedText>
                <ThemedText type="small">
                  Schedule: {event.allDay ? 'All-day event' : 'Timed event'}
                </ThemedText>
                <ThemedText type="small">Start: {formatDateTime(event.startAt)}</ThemedText>
                <ThemedText type="small">End: {formatDateTime(event.endAt)}</ThemedText>
                <ThemedText type="small">Created by: {event.createdBy.name}</ThemedText>
                <ThemedText type="small">Created at: {formatDateTime(event.createdAt)}</ThemedText>
                <ThemedText type="small">Updated at: {formatDateTime(event.updatedAt)}</ThemedText>
              </ThemedView>

              <ThemedView type="backgroundElement" style={styles.bodyBox}>
                <ThemedText>{event.description ?? 'No description'}</ThemedText>
              </ThemedView>
            </ThemedView>
          )}
        </ScrollView>
      </SafeAreaView>
    </ThemedView>
  );
}

function parseEventId(value: string | string[] | undefined): number | null {
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
