import { useFocusEffect, useRouter } from 'expo-router';
import { useCallback, useState } from 'react';
import { Button, FlatList, Platform, Pressable, RefreshControl, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { CalendarEventListItem, getCalendarEvents } from '@/api/calendar';
import { ApiError } from '@/api/types';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useAuth } from '@/hooks/useAuth';

export default function CalendarListScreen() {
  const router = useRouter();
  const theme = useTheme();
  const { isLoading } = useAuth();
  const [events, setEvents] = useState<CalendarEventListItem[]>([]);
  const [period, setPeriod] = useState(() => getCurrentMonthPeriod());
  const [isInitialLoading, setIsInitialLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const loadEvents = useCallback(async () => {
    setErrorMessage(null);

    try {
      const response = await getCalendarEvents(period.from, period.to);
      setEvents(response);
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage('Calendar events could not be loaded. Check the backend and try again.');
      }
    } finally {
      setIsInitialLoading(false);
      setIsRefreshing(false);
    }
  }, [period.from, period.to]);

  useFocusEffect(
    useCallback(() => {
      if (isLoading) {
        return;
      }

      void loadEvents();
    }, [isLoading, loadEvents]),
  );

  async function handleRefresh() {
    setIsRefreshing(true);
    await loadEvents();
  }

  function handleResetCurrentMonth() {
    setIsInitialLoading(true);
    setPeriod(getCurrentMonthPeriod());
  }

  function handleOpenEvent(eventId: number) {
    if (Platform.OS === 'web') {
      window.location.assign(`/calendar/${eventId}`);
      return;
    }

    router.push({
      pathname: '/calendar/[eventId]',
      params: { eventId: String(eventId) },
    } as never);
  }

  function renderEvent({ item }: { item: CalendarEventListItem }) {
    return (
      <Pressable
        style={({ pressed }) => [
          styles.eventCard,
          { backgroundColor: theme.backgroundElement },
          pressed && styles.pressed,
        ]}
        onPress={() => handleOpenEvent(item.id)}>
        <ThemedView type="backgroundElement" style={styles.eventContent}>
          <ThemedView type="backgroundElement" style={styles.eventHeader}>
            <ThemedText type="smallBold" style={styles.eventType}>
              {item.allDay ? 'ALL DAY' : 'TIMED'}
            </ThemedText>
          </ThemedView>
          <ThemedText type="smallBold">{item.title}</ThemedText>
          <ThemedText type="small" themeColor="textSecondary">
            {item.location ?? 'No location'}
          </ThemedText>
          <ThemedText type="small">
            {formatDateTime(item.startAt)} - {formatDateTime(item.endAt)}
          </ThemedText>
        </ThemedView>
      </Pressable>
    );
  }

  if (isLoading || isInitialLoading) {
    return (
      <ThemedView style={styles.container}>
        <SafeAreaView style={styles.safeArea}>
          <ThemedText type="subtitle">Loading calendar...</ThemedText>
        </SafeAreaView>
      </ThemedView>
    );
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedView style={styles.header}>
          <ThemedText type="subtitle">Calendar</ThemedText>
          <ThemedText type="small" themeColor="textSecondary">
            {period.from} - {period.to}
          </ThemedText>
          <ThemedView style={styles.refreshButton}>
            <Button title="Current month" onPress={handleResetCurrentMonth} />
          </ThemedView>
        </ThemedView>

        {errorMessage && (
          <ThemedView type="backgroundElement" style={styles.messageBox}>
            <ThemedText type="small" style={styles.errorText}>
              {errorMessage}
            </ThemedText>
          </ThemedView>
        )}

        <FlatList
          data={events}
          keyExtractor={(item) => String(item.id)}
          renderItem={renderEvent}
          contentContainerStyle={styles.listContent}
          refreshControl={
            <RefreshControl refreshing={isRefreshing} onRefresh={handleRefresh} />
          }
          ListEmptyComponent={
            <ThemedView type="backgroundElement" style={styles.messageBox}>
              <ThemedText type="small">No calendar events for the current month.</ThemedText>
            </ThemedView>
          }
        />
      </SafeAreaView>
    </ThemedView>
  );
}

function getCurrentMonthPeriod(): { from: string; to: string } {
  const now = new Date();
  const fromDate = new Date(now.getFullYear(), now.getMonth(), 1, 0, 0, 0);
  const toDate = new Date(now.getFullYear(), now.getMonth() + 1, 1, 0, 0, 0);

  return {
    from: formatLocalDateTime(fromDate),
    to: formatLocalDateTime(toDate),
  };
}

function formatLocalDateTime(date: Date): string {
  const year = date.getFullYear();
  const month = pad2(date.getMonth() + 1);
  const day = pad2(date.getDate());
  const hour = pad2(date.getHours());
  const minute = pad2(date.getMinutes());
  const second = pad2(date.getSeconds());

  return `${year}-${month}-${day}T${hour}:${minute}:${second}`;
}

function formatDateTime(value: string): string {
  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return date.toLocaleString();
}

function pad2(value: number): string {
  return String(value).padStart(2, '0');
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
    gap: Spacing.two,
    paddingBottom: Spacing.three,
    paddingTop: Spacing.four,
  },
  refreshButton: {
    alignSelf: 'stretch',
    borderRadius: Spacing.three,
    overflow: 'hidden',
  },
  listContent: {
    gap: Spacing.three,
    paddingBottom: Spacing.four,
  },
  eventCard: {
    borderRadius: Spacing.three,
  },
  eventContent: {
    borderRadius: Spacing.three,
    gap: Spacing.two,
    padding: Spacing.three,
  },
  eventHeader: {
    alignItems: 'flex-start',
  },
  eventType: {
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
  pressed: {
    opacity: 0.75,
  },
});
