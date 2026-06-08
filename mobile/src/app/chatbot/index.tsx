import { useRouter } from 'expo-router';
import { useState } from 'react';
import { FlatList, Platform, Pressable, StyleSheet, TextInput } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ChatbotMessage, sendChatbotMessage } from '@/api/chatbot';
import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useAuth } from '@/hooks/useAuth';

type ScreenMessage = ChatbotMessage & {
  id: number;
};

const INITIAL_MESSAGES: ScreenMessage[] = [
  {
    id: 1,
    role: 'assistant',
    content: 'I can help you find department notices, schedules, reservations, resources, and polls.',
  },
  {
    id: 2,
    role: 'assistant',
    content: 'Try asking: "How do I vote in a poll?" or "Where can I check reservations?"',
  },
];

export default function ChatbotScreen() {
  const router = useRouter();
  const theme = useTheme();
  const { isLoading } = useAuth();
  const [messages, setMessages] = useState<ScreenMessage[]>(INITIAL_MESSAGES);
  const [input, setInput] = useState('');
  const [isSending, setIsSending] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  async function handleSend() {
    const text = input.trim();

    if (!text || isSending) {
      return;
    }

    const userMessage: ScreenMessage = {
      id: Date.now(),
      role: 'user',
      content: text,
    };

    const nextMessages = [...messages, userMessage];
    setMessages(nextMessages);
    setInput('');
    setIsSending(true);
    setErrorMessage(null);

    try {
      const response = await sendChatbotMessage(
        text,
        nextMessages.map(({ role, content }) => ({ role, content })),
      );

      setMessages((current) => [
        ...current,
        {
          id: Date.now() + 1,
          role: 'assistant',
          content: response.message,
        },
      ]);
    } catch {
      setErrorMessage('The chatbot response could not be loaded. Please try again.');
    } finally {
      setIsSending(false);
    }
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

  function renderMessage({ item }: { item: ScreenMessage }) {
    const mine = item.role === 'user';

    return (
      <ThemedView style={[styles.messageRow, mine ? styles.myMessageRow : styles.botMessageRow]}>
        <ThemedView style={[styles.messageBubble, mine ? styles.myBubble : styles.botBubble]}>
          <ThemedText type="smallBold" style={mine ? styles.myText : styles.botName}>
            {mine ? 'Me' : 'LGB AI'}
          </ThemedText>
          <ThemedText style={mine ? styles.myText : undefined}>{item.content}</ThemedText>
        </ThemedView>
      </ThemedView>
    );
  }

  if (isLoading) {
    return (
      <ThemedView style={styles.container}>
        <SafeAreaView style={styles.safeArea}>
          <ThemedText type="subtitle">Preparing chatbot...</ThemedText>
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
              LGB AI Chatbot
            </ThemedText>
            <ThemedText type="small" themeColor="textSecondary">
              Ask about department life and core app workflows.
            </ThemedText>
          </ThemedView>
        </ThemedView>

        <FlatList
          data={messages}
          keyExtractor={(item) => String(item.id)}
          renderItem={renderMessage}
          contentContainerStyle={styles.messageList}
          ListFooterComponent={
            isSending ? (
              <ThemedView style={styles.botMessageRow}>
                <ThemedView style={[styles.messageBubble, styles.botBubble]}>
                  <ThemedText type="small">Writing a response...</ThemedText>
                </ThemedView>
              </ThemedView>
            ) : null
          }
        />

        {errorMessage && (
          <ThemedView type="backgroundElement" style={styles.errorBox}>
            <ThemedText type="small" style={styles.errorText}>
              {errorMessage}
            </ThemedText>
          </ThemedView>
        )}

        <ThemedView style={styles.composer}>
          <TextInput
            value={input}
            onChangeText={setInput}
            placeholder="Ask a question"
            placeholderTextColor={theme.textSecondary}
            editable={!isSending}
            style={[styles.input, { color: theme.text }]}
            onSubmitEditing={handleSend}
            returnKeyType="send"
          />
          <Pressable
            disabled={isSending}
            style={({ pressed }) => [
              styles.sendButton,
              isSending && styles.disabledButton,
              pressed && !isSending && styles.pressed,
            ]}
            onPress={handleSend}>
            <ThemedText type="smallBold" style={styles.sendButtonText}>
              {isSending ? 'Wait' : 'Send'}
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
  botMessageRow: {
    flexDirection: 'row',
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
  botBubble: {
    backgroundColor: '#ffffff',
    borderColor: '#e2e8f0',
    borderWidth: 1,
  },
  myBubble: {
    backgroundColor: '#0f766e',
  },
  botName: {
    color: '#0f766e',
  },
  myText: {
    color: '#ffffff',
  },
  errorBox: {
    borderRadius: Spacing.three,
    marginBottom: Spacing.two,
    padding: Spacing.three,
  },
  errorText: {
    color: '#dc2626',
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
    backgroundColor: '#0f766e',
    borderRadius: Spacing.two,
    minWidth: 72,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
  },
  disabledButton: {
    backgroundColor: '#94a3b8',
  },
  sendButtonText: {
    color: '#ffffff',
  },
  pressed: {
    opacity: 0.75,
  },
});
