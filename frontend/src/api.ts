export interface WeatherSnapshot {
  time: string;
  temperature: number;
  precipitation: number;
  windSpeed: number;
  cloudAreaFraction: number | null;
  relativeHumidity: number | null;
}

export interface GardeningRecommendation {
  score: number;
  status: 'PERFECT' | 'GOOD' | 'POSSIBLE' | 'WAIT';
  label: string;
  explanation: string;
  bestTimeStart: string;
  hoursUntilBestTime: number;
  missionModeText: string;
}

export interface GardeningResponse {
  recommendation: GardeningRecommendation;
  currentWeather: WeatherSnapshot;
  forecast24h: WeatherSnapshot[];
  badges: string[];
  guerillaTip: string;
  attribution: string;
}

const BASE_URL = import.meta.env.VITE_API_URL ?? '';

export async function fetchGardeningReadiness(lat: number, lon: number): Promise<GardeningResponse> {
  const url = `${BASE_URL}/api/gardening-readiness?lat=${lat}&lon=${lon}`;
  const response = await fetch(url);
  if (!response.ok) {
    const err = await response.json().catch(() => ({ error: 'Ukjent feil' }));
    throw new Error(err.error ?? `HTTP ${response.status}`);
  }
  return response.json();
}
