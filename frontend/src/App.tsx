import { useState, useEffect, useCallback } from 'react';
import { fetchGardeningReadiness, GardeningResponse } from './api';
import LocationSelector, { LOCATIONS, Location } from './components/LocationSelector';
import ReadinessCard from './components/ReadinessCard';
import ForecastDetails from './components/ForecastDetails';
import GardeningTips from './components/GardeningTips';

export default function App() {
  const [location, setLocation] = useState<Location>(LOCATIONS[0]);
  const [data, setData] = useState<GardeningResponse | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const load = useCallback(async (loc: Location) => {
    setLoading(true);
    setError(null);
    try {
      const result = await fetchGardeningReadiness(loc.lat, loc.lon);
      setData(result);
    } catch (e: unknown) {
      setError(e instanceof Error ? e.message : 'Klarte ikke hente data');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load(location);
  }, [location, load]);

  return (
    <div className="min-h-screen bg-guerilla-950 px-4 py-8">
      <div className="max-w-2xl mx-auto">
        {/* Header */}
        <div className="text-center mb-8">
          <div className="flex items-center justify-center gap-2 mb-2">
            <span className="text-3xl">🌱</span>
            <h1 className="text-3xl font-bold text-guerilla-300 tracking-tight">
              Garden Guerilla
            </h1>
          </div>
          <p className="text-sm text-guerilla-600 tracking-widest uppercase">Mission Control</p>
        </div>

        {/* Location selector */}
        <LocationSelector selected={location} onSelect={setLocation} />

        {/* Loading */}
        {loading && (
          <div className="flex flex-col items-center justify-center py-16 gap-3">
            <div className="w-8 h-8 border-2 border-guerilla-500 border-t-transparent rounded-full animate-spin"></div>
            <p className="text-sm text-guerilla-500">Henter værdata fra MET...</p>
          </div>
        )}

        {/* Error */}
        {error && !loading && (
          <div className="bg-red-950 border border-red-800 rounded-2xl p-5 text-center">
            <p className="text-red-400 text-sm">⚠️ {error}</p>
            <button
              onClick={() => load(location)}
              className="mt-3 px-4 py-2 bg-guerilla-800 text-guerilla-300 rounded-full text-sm hover:bg-guerilla-700"
            >
              Prøv igjen
            </button>
          </div>
        )}

        {/* Data */}
        {data && !loading && (
          <div className="space-y-4">
            <ReadinessCard
              recommendation={data.recommendation}
              badges={data.badges}
            />
            <ForecastDetails
              current={data.currentWeather}
              forecast24h={data.forecast24h}
            />
            <GardeningTips
              tip={data.guerillaTip}
              attribution={data.attribution}
            />
          </div>
        )}
      </div>
    </div>
  );
}
