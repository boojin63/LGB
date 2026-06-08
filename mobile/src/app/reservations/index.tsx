import { useFocusEffect, useRouter } from 'expo-router';
import { useCallback, useState } from 'react';
import { Button, FlatList, Platform, RefreshControl, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { getMyReservations, ReservationItem } from '@/api/reservations';
import { ApiError } from '@/api/types';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useAuth } from '@/hooks/useAuth';

const PAGE_SIZE = 10;

export default function ReservationListScreen() {
  const router = useRouter();
  const { isAuthenticated, isLoading } = useAuth();
  const [reservations, setReservations] = useState<ReservationItem[]>([]);
  const [isInitialLoading, setIsInitialLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const loadReservations = useCallback(async () => {
    setErrorMessage(null);

    try {
      const response = await getMyReservations(0, PAGE_SIZE);
      setReservations(response.content);
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage('Reservations could not be loaded. Check the backend and try again.');
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

      void loadReservations();
    }, [isAuthenticated, isLoading, loadReservations]),
  );

  async function handleRefresh() {
    setIsRefreshing(true);
    await loadReservations();
  }

  function handleNewReservation() {
    if (Platform.OS === 'web') {
      window.location.assign('/reservations/new');
      return;
    }

    router.push('/reservations/new' as never);
  }

  function renderReservation({ item }: { item: ReservationItem }) {
    return (
      <ThemedView type="backgroundElement" style={styles.card}>
        <ThemedView type="backgroundElement" style={styles.cardHeader}>
          <ThemedText type="smallBold" style={styles.statusText}>
            {item.status}
          </ThemedText>
        </ThemedView>
        <ThemedText type="smallBold">{item.resource.name}</ThemedText>
        <ThemedText type="small">Start: {formatDateTime(item.startAt)}</ThemedText>
        <ThemedText type="small">End: {formatDateTime(item.endAt)}</ThemedText>
        <ThemedText type="small">Purpose: {item.purpose}</ThemedText>
        {item.rejectReason && (
          <ThemedText type="small" style={styles.errorText}>
            Rejected: {item.rejectReason}
          </ThemedText>
        )}
      </ThemedView>
    );
  }

  if (isLoading || isInitialLoading) {
    return (
      <ThemedView style={styles.container}>
        <SafeAreaView style={styles.safeArea}>
          <ThemedText type="subtitle">Loading reservations...</ThemedText>
        </SafeAreaView>
      </ThemedView>
    );
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedView style={styles.header}>
          <ThemedText type="subtitle">My Reservations</ThemedText>
          <ThemedView style={styles.buttonWrap}>
            <Button title="New reservation" onPress={handleNewReservation} />
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
          data={reservations}
          keyExtractor={(item) => String(item.id)}
          renderItem={renderReservation}
          contentContainerStyle={styles.listContent}
          refreshControl={
            <RefreshControl refreshing={isRefreshing} onRefresh={handleRefresh} />
          }
          ListEmptyComponent={
            <ThemedView type="backgroundElement" style={styles.messageBox}>
              <ThemedText type="small">No reservations yet.</ThemedText>
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
    gap: Spacing.two,
    paddingBottom: Spacing.three,
    paddingTop: Spacing.four,
  },
  buttonWrap: {
    borderRadius: Spacing.three,
    overflow: 'hidden',
  },
  listContent: {
    gap: Spacing.three,
    paddingBottom: Spacing.four,
  },
  card: {
    borderRadius: Spacing.three,
    gap: Spacing.two,
    padding: Spacing.three,
  },
  cardHeader: {
    alignItems: 'flex-start',
  },
  statusText: {
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
});
