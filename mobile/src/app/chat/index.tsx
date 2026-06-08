import { useRouter } from 'expo-router';
import { useState } from 'react';
import { Button, FlatList, Platform, StyleSheet, TextInput } from 'react-native';
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
    text: '안녕하세요! 소프트웨어융합학과 공지방입니다.',
  },
  {
    id: 2,
    sender: 'department',
    text: '오늘 세미나 참석 가능한 분들은 투표에 참여해주세요.',
  },
  {
    id: 3,
    sender: 'department',
    text: '예약 기능도 모바일에서 확인할 수 있습니다.',
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
      <ThemedView
        style={[
          styles.messageRow,
          mine ? styles.myMessageRow : styles.departmentMessageRow,
        ]}>
        <ThemedView
          style={[
            styles.messageBubble,
            { backgroundColor: mine ? '#2563eb' : theme.backgroundElement },
          ]}>
          <ThemedText type="smallBold" style={mine ? styles.myText : undefined}>
            {mine ? '나' : '학과방'}
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
          <ThemedText type="subtitle">채팅을 준비하는 중...</ThemedText>
        </SafeAreaView>
      </ThemedView>
    );
  }

  return (
    <ThemedView style={styles.container}>
      <SafeAreaView style={styles.safeArea}>
        <ThemedView style={styles.header}>
          <ThemedView style={styles.backWrap}>
            <Button title="뒤로" onPress={handleBack} />
          </ThemedView>
          <ThemedText type="subtitle">학과 채팅</ThemedText>
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
            placeholder="메시지를 입력하세요"
            style={[styles.input, { color: theme.text, borderColor: theme.textSecondary }]}
            onSubmitEditing={handleSend}
          />
          <ThemedView style={styles.sendWrap}>
            <Button title="전송" onPress={handleSend} />
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
    paddingBottom: BottomTabInset + Spacing.three,
    paddingHorizontal: Spacing.four,
  },
  header: {
    gap: Spacing.two,
    paddingBottom: Spacing.three,
    paddingTop: Spacing.four,
  },
  backWrap: {
    alignSelf: 'flex-start',
    borderRadius: Spacing.three,
    overflow: 'hidden',
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
  myText: {
    color: '#ffffff',
  },
  composer: {
    flexDirection: 'row',
    gap: Spacing.two,
    paddingBottom: Spacing.two,
  },
  input: {
    borderRadius: Spacing.two,
    borderWidth: 1,
    flex: 1,
    paddingHorizontal: Spacing.three,
    paddingVertical: Spacing.two,
  },
  sendWrap: {
    borderRadius: Spacing.three,
    overflow: 'hidden',
  },
});
