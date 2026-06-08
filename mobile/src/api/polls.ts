import { apiGet, apiPost } from './client';
import { PageResponse } from './types';

export type PollStatus = 'OPEN' | 'CLOSED';

export type PollListItem = {
  id: number;
  title: string;
  status: PollStatus;
  anonymous: boolean;
  resultVisible: boolean;
  startsAt: string;
  endsAt: string;
  hasVoted: boolean;
  createdAt: string;
};

export type PollOption = {
  id: number;
  text: string;
  displayOrder: number;
};

export type PollDetail = {
  id: number;
  title: string;
  description: string | null;
  status: PollStatus;
  anonymous: boolean;
  resultVisible: boolean;
  startsAt: string;
  endsAt: string;
  options: PollOption[];
  hasVoted: boolean;
  createdAt: string;
  updatedAt: string;
};

export type PollVoteResponse = {
  pollId: number;
  optionId: number;
  createdAt: string;
};

export type PollResultOption = {
  optionId: number;
  text: string;
  displayOrder: number;
  voteCount: number;
  percentage: number;
};

export type PollResult = {
  pollId: number;
  title: string;
  totalVotes: number;
  options: PollResultOption[];
  anonymous: boolean;
  resultVisible: boolean;
  status: PollStatus;
  startsAt: string;
  endsAt: string;
};

export type PollPage = PageResponse<PollListItem>;

export function getPolls(page = 0, size = 10): Promise<PollPage> {
  return apiGet<PollPage>('/api/polls', { page, size });
}

export function getPoll(pollId: number): Promise<PollDetail> {
  return apiGet<PollDetail>(`/api/polls/${pollId}`);
}

export function votePoll(pollId: number, optionId: number): Promise<PollVoteResponse> {
  return apiPost<PollVoteResponse>(`/api/polls/${pollId}/vote`, { optionId });
}

export function getPollResult(pollId: number): Promise<PollResult> {
  return apiGet<PollResult>(`/api/polls/${pollId}/result`);
}
