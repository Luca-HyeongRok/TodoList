const API_BASE = import.meta.env.VITE_API_BASE_URL || "";
export const BASE_URL = API_BASE ? `${API_BASE}/api` : "/api";

export const MOCK_MODE = import.meta.env.VITE_MOCK_MODE === "true";
export const MOCK_USER = {
  userId: "mockUser",
  username: "목업 사용자",
};
