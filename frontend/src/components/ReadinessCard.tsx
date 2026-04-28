import { GardeningRecommendation } from '../api';

interface Props {
  recommendation: GardeningRecommendation;
  badges: string[];
}

const STATUS_CONFIG = {
  PERFECT: { bg: 'from-emerald-900 to-slate-900', border: 'border-emerald-500', dot: 'bg-emerald-400', pulse: 'animate-pulse', ring: '#4ade80' },
  GOOD:    { bg: 'from-slate-800 to-slate-950',   border: 'border-sky-600',     dot: 'bg-sky-400',     pulse: '',             ring: '#38bdf8' },
  POSSIBLE:{ bg: 'from-amber-950 to-slate-900',   border: 'border-amber-600',   dot: 'bg-amber-400',   pulse: '',             ring: '#fbbf24' },
  WAIT:    { bg: 'from-red-950 to-slate-900',      border: 'border-red-700',     dot: 'bg-red-500',     pulse: '',             ring: '#ef4444' },
};

export default function ReadinessCard({ recommendation, badges }: Props) {
  const config = STATUS_CONFIG[recommendation.status] ?? STATUS_CONFIG.WAIT;
  const circumference = 2 * Math.PI * 45;
  const dashOffset = circumference - (recommendation.score / 100) * circumference;

  return (
    <div className={`bg-gradient-to-br ${config.bg} border ${config.border} rounded-2xl p-6`}>
      {/* Header */}
      <div className="flex items-center gap-2 mb-4">
        <span className={`w-3 h-3 rounded-full ${config.dot} ${config.pulse}`}></span>
        <span className="text-xs font-mono uppercase tracking-widest text-slate-400">Mission Status</span>
      </div>

      {/* Score ring + label */}
      <div className="flex items-center gap-6 mb-4">
        <div className="relative w-28 h-28 flex-shrink-0">
          <svg viewBox="0 0 100 100" className="w-full h-full -rotate-90">
            <circle cx="50" cy="50" r="45" fill="none" stroke="#1e293b" strokeWidth="8"/>
            <circle
              cx="50" cy="50" r="45" fill="none"
              stroke={config.ring}
              strokeWidth="8"
              strokeLinecap="round"
              strokeDasharray={circumference}
              strokeDashoffset={dashOffset}
              className="score-ring"
            />
          </svg>
          <div className="absolute inset-0 flex flex-col items-center justify-center">
            <span className="text-3xl font-bold text-white">{recommendation.score}</span>
            <span className="text-xs text-slate-400">/ 100</span>
          </div>
        </div>

        <div className="flex-1">
          <h2 className="text-xl font-bold text-white mb-1">{recommendation.label}</h2>
          <p className="text-sm text-slate-300 mb-3">{recommendation.explanation}</p>
          <p className="text-xs text-slate-400 italic">{recommendation.missionModeText}</p>
        </div>
      </div>

      {/* Badges */}
      {badges.length > 0 && (
        <div className="flex flex-wrap gap-2">
          {badges.map((badge) => (
            <span key={badge} className="px-2 py-1 bg-slate-900/70 border border-slate-600 rounded-full text-xs text-slate-300">
              {badge}
            </span>
          ))}
        </div>
      )}
    </div>
  );
}
