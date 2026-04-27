import { WeatherSnapshot } from '../api';

interface Props {
  current: WeatherSnapshot;
  forecast24h: WeatherSnapshot[];
}

function formatTime(iso: string): string {
  return new Date(iso).toLocaleTimeString('no-NO', { hour: '2-digit', minute: '2-digit' });
}

function TempBar({ value, max = 30 }: { value: number; max?: number }) {
  const pct = Math.max(0, Math.min(100, ((value + 10) / (max + 10)) * 100));
  return (
    <div className="w-full bg-guerilla-950 rounded-full h-1.5 mt-1">
      <div
        className="h-1.5 rounded-full bg-guerilla-400"
        style={{ width: `${pct}%` }}
      />
    </div>
  );
}

export default function ForecastDetails({ current, forecast24h }: Props) {
  return (
    <div className="bg-guerilla-950 border border-guerilla-800 rounded-2xl p-5">
      <h3 className="text-xs uppercase tracking-widest text-guerilla-500 mb-4">📡 Værstatus nå</h3>

      <div className="grid grid-cols-2 gap-3 mb-5">
        <Stat icon="🌡️" label="Temperatur" value={`${current.temperature.toFixed(1)}°C`} />
        <Stat icon="🌧️" label="Nedbør (1t)" value={`${current.precipitation.toFixed(1)} mm`} />
        <Stat icon="💨" label="Vind" value={`${current.windSpeed.toFixed(1)} m/s`} />
        {current.cloudAreaFraction != null && (
          <Stat icon="☁️" label="Skydekke" value={`${current.cloudAreaFraction.toFixed(0)}%`} />
        )}
        {current.relativeHumidity != null && (
          <Stat icon="💧" label="Luftfuktighet" value={`${current.relativeHumidity.toFixed(0)}%`} />
        )}
      </div>

      {/* 24h mini forecast */}
      <h3 className="text-xs uppercase tracking-widest text-guerilla-500 mb-3">📈 Neste 24 timer</h3>
      <div className="overflow-x-auto">
        <div className="flex gap-2 min-w-max pb-1">
          {forecast24h.slice(0, 12).map((snap, i) => (
            <div key={i} className="flex flex-col items-center bg-guerilla-900 rounded-lg px-2 py-2 min-w-[52px]">
              <span className="text-xs text-guerilla-500">{formatTime(snap.time)}</span>
              <span className="text-sm font-semibold text-white mt-1">{snap.temperature.toFixed(0)}°</span>
              <TempBar value={snap.temperature} />
              <span className="text-xs text-blue-400 mt-1">{snap.precipitation.toFixed(1)}mm</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

function Stat({ icon, label, value }: { icon: string; label: string; value: string }) {
  return (
    <div className="bg-guerilla-900 rounded-xl p-3">
      <div className="text-lg">{icon}</div>
      <div className="text-xs text-guerilla-500 mt-1">{label}</div>
      <div className="text-base font-semibold text-white">{value}</div>
    </div>
  );
}
