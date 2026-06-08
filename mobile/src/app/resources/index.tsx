import { useFocusEffect } from 'expo-router';
import { useCallback, useState } from 'react';
import { FlatList, RefreshControl, StyleSheet } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { getResources, ResourceItem } from '@/api/resources';
import { ApiError } from '@/api/types';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useAuth } from '@/hooks/useAuth';

export default function ResourceListScreen() {
  const { isAuthenticated, isLoading } = useAuth();
  const [resources, setResources] = useState<ResourceItem[]>([]);
  const [isInitialLoading, setIsInitialLoading] = useState(true);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const loadResources = useCallback(async () => {
    setErrorMessage(null);

    try {
      const response = await getResources();
      setResources(response);
    } catch (error) {
      if (error instanceof ApiError) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage('Failed to load resources.');
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

      void loadResources();
    }, [isAuthenticated, isLoading, loadResources]),
  );

  async function handleRefresh() {
    setIsRefreshing(true);
    await loadResources();
  }

  function renderResource({ item }: { item: ResourceItem }) {
    return (
      <ThemedView type="backgroundElement" style={styles.card}>
        <ThemedText type="smallBold">{item.name}</ThemedText>
        <ThemedText type="small">ID: {item.id}</ThemedText>
        <ThemedText type="small">Type: {item.type}</ThemedText>
        <ThemedText type="small">Location: {item.location ?? 'No location'}</ThemedText>
        <ThemedText type="small">Active: {item.active ? 'Yes' : 'No'}</ThemedText>
      </ThemedView>
    );
  }

  if (isLoading || isInitialLoading) {
    return (
      <ThemedView style={styles.container}>
        <SafeAreaView style={styles.safeArea}>
          <ThemedText type="subtitle">Loading resources...</ThemedText>
        </SafeAreaView>
      </ThemedView>
    );
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedView style={styles.header}>
          <ThemedText type="subtitle">Resources</ThemedText>
          <ThemedText type="small" themeColor="textSecondary">
            Available reservation resources.
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
          data={resources}
          keyExtractor={(item) => String(item.id)}
          renderItem={renderResource}
          contentContainerStyle={styles.listContent}
          refreshControl={
            <RefreshControl refreshing={isRefreshing} onRefresh={handleRefresh} />
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
  header: {
    gap: Spacing.one,
    paddingBottom: Spacing.three,
    paddingTop: Spacing.four,
  },
  listContent: {
    gap: Spacing.three,
    paddingBottom: Spacing.four,
  },
  card: {
    borderRadius: Spacing.three,
    gap: Spacing.one,
    padding: Spacing.three,
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
