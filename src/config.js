const API_BASE = import.meta.env.VITE_API_BASE_URL || "";
export const BASE_URL = API_BASE ? `${API_BASE}/api` : "/api";
