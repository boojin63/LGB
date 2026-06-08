import { useRouter } from 'expo-router';
import { useState } from 'react';
import { FlatList, Platform, Pressable, StyleSheet, TextInput } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useAuth } from '@/hooks/useAuth';

type ChatMessage = {
  id: number;
  sender: 'department' | 'me';
  text: string;
};

const INITIAL_MESSAGES: ChatMessage[] = [
  {
    id: 1,
    sender: 'department',
    text: 'Welcome to the department chat room.',
  },
  {
    id: 2,
    sender: 'department',
    text: 'Please check today\'s poll and share any questions here.',
  },
  {
    id: 3,
    sender: 'department',
    text: 'Reservation status can be checked from the mobile menu.',
  },
];

export default function ChatScreen() {
  const router = useRouter();
  const theme = useTheme();
  const { isLoading } = useAuth();
  const [messages, setMessages] = useState<ChatMessage[]>(INITIAL_MESSAGES);
  const [input, setInput] = useState('');

  function handleSend() {
    const text = input.trim();

    if (!text) {
      return;
    }

    setMessages((current) => [
      ...current,
      {
        id: Date.now(),
        sender: 'me',
        text,
      },
    ]);
    setInput('');
  }

  function handleBack() {
    if (router.canGoBack()) {
      router.back();
      return;
    }

    if (Platform.OS === 'web') {
      window.location.assign('/');
      return;
    }

    router.replace('/');
  }

  function renderMessage({ item }: { item: ChatMessage }) {
    const mine = item.sender === 'me';

    return (
      <ThemedView style={[styles.messageRow, mine ? styles.myMessageRow : styles.departmentMessageRow]}>
        <ThemedView style={[styles.messageBubble, mine ? styles.myBubble : styles.departmentBubble]}>
          <ThemedText type="smallBold" style={mine ? styles.myText : styles.senderText}>
            {mine ? 'Me' : 'Department'}
          </ThemedText>
          <ThemedText style={mine ? styles.myText : undefined}>{item.text}</ThemedText>
        </ThemedView>
      </ThemedView>
    );
  }

  if (isLoading) {
    return (
      <ThemedView style={styles.container}>
        <SafeAreaView style={styles.safeArea}>
          <ThemedText type="subtitle">Preparing chat...</ThemedText>
        </SafeAreaView>
      </ThemedView>
    );
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedView style={styles.header}>
          <Pressable style={({ pressed }) => [styles.secondaryButton, pressed && styles.pressed]} onPress={handleBack}>
            <ThemedText type="smallBold" style={styles.secondaryButtonText}>
              Back
            </ThemedText>
          </Pressable>
          <ThemedView style={styles.headerText}>
            <ThemedText type="subtitle" style={styles.title}>
              Department Chat
            </ThemedText>
            <ThemedText type="small" themeColor="textSecondary">
              Messages stay on this screen during the mobile demo.
            </ThemedText>
          </ThemedView>
        </ThemedView>

        <FlatList
          data={messages}
          keyExtractor={(item) => String(item.id)}
          renderItem={renderMessage}
          contentContainerStyle={styles.messageList}
        />

        <ThemedView style={styles.composer}>
          <TextInput
            value={input}
            onChangeText={setInput}
            placeholder="Type a message"
            placeholderTextColor={theme.textSecondary}
            style={[styles.input, { color: theme.text }]}
            onSubmitEditing={handleSend}
            returnKeyType="send"
          />
          <Pressable style={({ pressed }) => [styles.sendButton, pressed && styles.pressed]} onPress={handleSend}>
            <ThemedText type="smallBold" style={styles.sendButtonText}>
              Send
            </ThemedText>
          </Pressable>
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
    paddingBottom: BottomTabInset + Spacing.three,
    paddingHorizontal: Spacing.four,
  },
  header: {
    gap: Spacing.three,
    paddingBottom: Spacing.three,
    paddingTop: Spacing.four,
  },
  headerText: {
    gap: Spacing.one,
  },
  title: {
    fontSize: 28,
    lineHeight: 36,
  },
  secondaryButton: {
    alignSelf: 'flex-start',
    backgroundColor: '#e0f2fe',
    borderRadius: Spacing.three,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
  },
  secondaryButtonText: {
    color: '#0369a1',
  },
  messageList: {
    gap: Spacing.two,
    paddingBottom: Spacing.three,
  },
  messageRow: {
    flexDirection: 'row',
  },
  departmentMessageRow: {
    justifyContent: 'flex-start',
  },
  myMessageRow: {
    justifyContent: 'flex-end',
  },
  messageBubble: {
    borderRadius: Spacing.three,
    gap: Spacing.one,
    maxWidth: '82%',
    padding: Spacing.three,
  },
  departmentBubble: {
    backgroundColor: '#ffffff',
    borderColor: '#e2e8f0',
    borderWidth: 1,
  },
  myBubble: {
    backgroundColor: '#2563eb',
  },
  senderText: {
    color: '#0f766e',
  },
  myText: {
    color: '#ffffff',
  },
  composer: {
    alignItems: 'center',
    backgroundColor: '#ffffff',
    borderColor: '#e2e8f0',
    borderRadius: Spacing.three,
    borderWidth: 1,
    flexDirection: 'row',
    gap: Spacing.two,
    padding: Spacing.two,
  },
  input: {
    flex: 1,
    minHeight: 44,
    paddingHorizontal: Spacing.two,
  },
  sendButton: {
    alignItems: 'center',
    backgroundColor: '#2563eb',
    borderRadius: Spacing.two,
    minWidth: 72,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
  },
  sendButtonText: {
    color: '#ffffff',
  },
  pressed: {
    opacity: 0.75,
  },
});
