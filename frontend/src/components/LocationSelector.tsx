export interface Location {
  name: string;
  lat: number;
  lon: number;
  emoji: string;
}

export const LOCATIONS: Location[] = [
  { name: 'Oslo', lat: 59.9139, lon: 10.7522, emoji: '🏙️' },
  { name: 'Bergen', lat: 60.3913, lon: 5.3221, emoji: '🌊' },
  { name: 'Trondheim', lat: 63.4305, lon: 10.3951, emoji: '⚓' },
  { name: 'Stavanger', lat: 58.9700, lon: 5.7331, emoji: '🛢️' },
  { name: 'Kristiansand', lat: 58.1467, lon: 7.9956, emoji: '⛵' },
  { name: 'Tromsø', lat: 69.6496, lon: 18.9560, emoji: '🌌' },
];

interface Props {
  selected: Location;
  onSelect: (loc: Location) => void;
}

export default function LocationSelector({ selected, onSelect }: Props) {
  return (
    <div className="flex flex-wrap gap-2 justify-center mb-6">
      {LOCATIONS.map((loc) => (
        <button
          key={loc.name}
          onClick={() => onSelect(loc)}
          className={`px-4 py-2 rounded-full text-sm font-semibold border transition-all duration-200
            ${selected.name === loc.name
              ? 'bg-guerilla-600 border-guerilla-400 text-white shadow-lg mission-glow'
              : 'bg-slate-900 border-slate-700 text-slate-400 hover:border-slate-500 hover:text-slate-200'
            }`}
        >
          {loc.emoji} {loc.name}
        </button>
      ))}
    </div>
  );
}
