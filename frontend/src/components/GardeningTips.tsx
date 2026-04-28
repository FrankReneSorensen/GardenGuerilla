interface Props {
  tip: string;
  attribution: string;
}

const FACTS = [
  { icon: '🌧️', title: 'Lett regn er gull',  text: 'Frø får naturlig vanning og bedre jordkontakt.',       border: 'border-sky-700',    iconBg: 'bg-sky-950' },
  { icon: '☁️', title: 'Overskyet = bra',     text: 'Mindre fordamping og mindre stress for spirer.',       border: 'border-slate-600',  iconBg: 'bg-slate-800' },
  { icon: '💨', title: 'Vind er fienden',      text: 'Tørker ut jord og småplanter raskt.',                 border: 'border-cyan-700',   iconBg: 'bg-cyan-950' },
  { icon: '❄️', title: 'Frost er kritisk',     text: 'Kan drepe nye spirer. Vent til minst 2°C om natten.', border: 'border-indigo-700', iconBg: 'bg-indigo-950' },
];

export default function GardeningTips({ tip, attribution }: Props) {
  return (
    <div className="space-y-4">
      {/* Daily tip */}
      <div className="bg-slate-900 border border-amber-700/60 rounded-2xl p-5">
        <div className="flex items-center gap-2 mb-2">
          <span className="text-lg">💣</span>
          <h3 className="text-xs uppercase tracking-widest text-amber-500">Guerilla Tips of the Day</h3>
        </div>
        <p className="text-sm text-slate-200">{tip}</p>
      </div>

      {/* Facts grid */}
      <div className="grid grid-cols-2 gap-3">
        {FACTS.map((f) => (
          <div key={f.title} className={`bg-slate-900 border ${f.border} rounded-xl p-3`}>
            <div className={`text-xl mb-1 w-8 h-8 flex items-center justify-center rounded-lg ${f.iconBg}`}>{f.icon}</div>
            <div className="text-xs font-semibold text-slate-200 mb-1">{f.title}</div>
            <div className="text-xs text-slate-500">{f.text}</div>
          </div>
        ))}
      </div>

      {/* Plant list */}
      <div className="bg-slate-900 border border-emerald-800/50 rounded-xl p-4">
        <h3 className="text-xs uppercase tracking-widest text-emerald-500 mb-3">🌿 Robuste planter for Norge</h3>
        <div className="flex flex-wrap gap-2">
          {['Rødkløver 🍀', 'Prestekrage 🌼', 'Blåklokke 🔔', 'Valmue 🌺', 'Kornblomst 💙'].map((p) => (
            <span key={p} className="px-2 py-1 bg-slate-800 border border-slate-600 rounded-full text-xs text-slate-300">{p}</span>
          ))}
        </div>
      </div>

      {/* Attribution */}
      <p className="text-xs text-slate-600 text-center">{attribution}</p>
    </div>
  );
}
