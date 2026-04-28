import { LOCATIONS, Location } from './LocationSelector';

interface Props {
  onEnterMission: (location?: Location) => void;
}

const POSTER_URL = '/garden-guerilla-poster.jpg';

const CATEGORIES = [
  { icon: '🐝', label: 'Pollinatorer' },
  { icon: '🐦', label: 'Fugler' },
  { icon: '🐞', label: 'Skadedyr' },
  { icon: '🦋', label: 'Biodiversitet' },
  { icon: '🌱', label: 'Jordhelse' },
];

export default function HeroPage({ onEnterMission }: Props) {
  return (
    <div className="relative min-h-screen flex flex-col">
      {/* Full-screen background poster */}
      <div
        className="absolute inset-0 bg-cover bg-center bg-no-repeat"
        style={{ backgroundImage: `url('${POSTER_URL}')` }}
        aria-hidden="true"
      />

      {/* Dark gradient overlay — heavier at top and bottom, lighter in middle */}
      <div
        className="absolute inset-0"
        style={{
          background:
            'linear-gradient(to bottom, rgba(5,46,22,0.55) 0%, rgba(5,46,22,0.05) 35%, rgba(5,46,22,0.05) 55%, rgba(5,46,22,0.82) 100%)',
        }}
        aria-hidden="true"
      />

      {/* ── Top bar: site title + tagline ── */}
      <header className="relative z-10 pt-6 px-4 text-center">
        <p className="text-xs font-mono uppercase tracking-[0.3em] text-guerilla-300 drop-shadow">
          ⚔️ Vi kjemper tilbake. Vi dyrker. Vi vinner. ⚔️
        </p>
      </header>

      {/* Spacer — pushes content to bottom */}
      <div className="flex-1" />

      {/* ── Bottom overlay: buttons & links ── */}
      <footer className="relative z-10 pb-8 px-4 flex flex-col items-center gap-5 max-w-lg mx-auto w-full">
        {/* Primary CTA */}
        <button
          onClick={() => onEnterMission()}
          className="w-full py-4 bg-guerilla-700/90 border-2 border-guerilla-400 text-white font-bold text-lg uppercase tracking-widest rounded-lg shadow-lg hover:bg-guerilla-600 hover:border-guerilla-300 transition-all duration-200 mission-glow"
        >
          🌱 Mission Control
        </button>

        {/* City quick-launch grid */}
        <div className="w-full">
          <p className="text-center text-xs font-mono uppercase tracking-widest text-guerilla-400 mb-2">
            Velg din by
          </p>
          <div className="grid grid-cols-3 gap-2">
            {LOCATIONS.map((loc) => (
              <button
                key={loc.name}
                onClick={() => onEnterMission(loc)}
                className="py-2 px-3 bg-guerilla-950/80 border border-guerilla-700 text-guerilla-300 rounded-lg text-sm font-semibold hover:bg-guerilla-800/90 hover:border-guerilla-500 hover:text-white transition-all duration-200"
              >
                {loc.emoji} {loc.name}
              </button>
            ))}
          </div>
        </div>

        {/* Category strip — mirrors the poster's bottom row */}
        <div className="w-full border-t border-guerilla-700/60 pt-4">
          <div className="flex justify-between items-center gap-1">
            {CATEGORIES.map((cat) => (
              <button
                key={cat.label}
                onClick={() => onEnterMission()}
                className="flex flex-col items-center gap-1 px-2 py-1 text-guerilla-400 hover:text-guerilla-200 transition-colors duration-150"
                title={cat.label}
              >
                <span className="text-xl">{cat.icon}</span>
                <span className="text-[10px] font-mono uppercase tracking-wider hidden sm:block">
                  {cat.label}
                </span>
              </button>
            ))}
          </div>
        </div>
      </footer>
    </div>
  );
}
