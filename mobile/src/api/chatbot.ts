export type ChatbotRole = 'user' | 'assistant';

export type ChatbotMessage = {
  role: ChatbotRole;
  content: string;
};

export type ChatbotRequest = {
  message: string;
  history?: ChatbotMessage[];
};

export type ChatbotResponse = {
  message: string;
};

export async function sendChatbotMessage(
  message: string,
  history: ChatbotMessage[] = [],
): Promise<ChatbotResponse> {
  const request: ChatbotRequest = { message, history };

  return Promise.resolve({
    message: createGuidanceReply(request.message),
  });
}

function createGuidanceReply(question: string): string {
  const normalized = question.toLowerCase();

  if (normalized.includes('notice') || normalized.includes('announcement')) {
    return 'Open Notices to review department announcements, pinned items, author, date, and view count.';
  }

  if (normalized.includes('calendar') || normalized.includes('schedule')) {
    return 'Open Calendar to check this month\'s department events and event detail pages.';
  }

  if (normalized.includes('reservation') || normalized.includes('reserve') || normalized.includes('room')) {
    return 'Open Reservations to review your requests or create a new reservation with resource, time, and purpose.';
  }

  if (normalized.includes('resource') || normalized.includes('equipment')) {
    return 'Open Resources to check reservable rooms, equipment type, location, and active status.';
  }

  if (normalized.includes('poll') || normalized.includes('vote')) {
    return 'Open Polls to join active student polls and view results when the result is available.';
  }

  if (normalized.includes('login') || normalized.includes('password')) {
    return 'For login issues, clear browser storage, return to the login page, and use the local seed account.';
  }

  return 'I can guide you to Notices, Calendar, Reservations, Resources, and Polls. Ask what you want to do next.';
}
