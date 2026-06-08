import { useRouter } from 'expo-router';
import { useState } from 'react';
import { Button, FlatList, Platform, StyleSheet, TextInput } from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';

import { ThemedText } from '@/components/themed-text';
import { ThemedView } from '@/components/themed-view';
import { BottomTabInset, MaxContentWidth, Spacing } from '@/constants/theme';
import { useTheme } from '@/hooks/use-theme';
import { useAuth } from '@/hooks/useAuth';

type BotMessage = {
  id: number;
  sender: 'bot' | 'me';
  text: string;
};

const INITIAL_MESSAGES: BotMessage[] = [
  {
    id: 1,
    sender: 'bot',
    text: '안녕하세요. 학과 앱 사용을 도와드릴게요. 공지, 일정, 예약, 투표에 대해 질문해보세요.',
  },
];

export default function ChatbotScreen() {
  const router = useRouter();
  const theme = useTheme();
  const { isLoading } = useAuth();
  const [messages, setMessages] = useState<BotMessage[]>(INITIAL_MESSAGES);
  const [input, setInput] = useState('');

  function handleSend() {
    const text = input.trim();

    if (!text) {
      return;
    }

    const createdAt = Date.now();
    setMessages((current) => [
      ...current,
      {
        id: createdAt,
        sender: 'me',
        text,
      },
      {
        id: createdAt + 1,
        sender: 'bot',
        text: createBotReply(text),
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

  function renderMessage({ item }: { item: BotMessage }) {
    const mine = item.sender === 'me';

    return (
      <ThemedView style={[styles.messageRow, mine ? styles.myMessageRow : styles.botMessageRow]}>
        <ThemedView
          style={[
            styles.messageBubble,
            { backgroundColor: mine ? '#2563eb' : theme.backgroundElement },
          ]}>
          <ThemedText type="smallBold" style={mine ? styles.myText : undefined}>
            {mine ? '나' : 'LGB AI'}
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
          <ThemedText type="subtitle">챗봇을 준비하는 중...</ThemedText>
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
          <ThemedText type="subtitle">LGB AI 챗봇</ThemedText>
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
            placeholder="질문을 입력하세요"
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

function createBotReply(question: string): string {
  const normalized = question.toLowerCase();

  if (normalized.includes('공지')) {
    return '공지사항은 홈의 공지 메뉴에서 확인할 수 있습니다.';
  }

  if (normalized.includes('일정')) {
    return '학과 일정은 일정 보기 메뉴에서 월별로 확인할 수 있습니다.';
  }

  if (normalized.includes('예약')) {
    return '예약 메뉴에서 자원을 선택하고 사용 시간을 입력해 신청할 수 있습니다.';
  }

  if (normalized.includes('투표')) {
    return '투표 메뉴에서 진행 중인 투표에 참여하고 결과를 확인할 수 있습니다.';
  }

  if (normalized.includes('로그인')) {
    return '로그인 문제가 있으면 학과 관리자에게 문의해주세요.';
  }

  return '현재는 MVP 챗봇입니다. 공지, 일정, 예약, 투표에 대해 질문해보세요.';
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
  botMessageRow: {
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
