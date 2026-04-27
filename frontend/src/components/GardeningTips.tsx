
interface Props {
  tip: string;
  attribution: string;
}

const FACTS = [
  { icon: '🌧️', title: 'Lett regn er gull', text: 'Frø får naturlig vanning og bedre jordkontakt.' },
  { icon: '☁️', title: 'Overskyet = bra', text: 'Mindre fordamping og mindre stress for spirer.' },
  { icon: '💨', title: 'Vind er fienden', text: 'Tørker ut jord og småplanter raskt.' },
  { icon: '❄️', title: 'Frost er kritisk', text: 'Kan drepe nye spirer. Vent til minst 2°C om natten.' },
];

export default function GardeningTips({ tip, attribution }: Props) {
  return (
    <div className="space-y-4">
      {/* Daily tip */}
      <div className="bg-guerilla-900 border border-guerilla-700 rounded-2xl p-5">
        <div className="flex items-center gap-2 mb-2">
          <span className="text-lg">💣</span>
          <h3 className="text-xs uppercase tracking-widest text-guerilla-400">Guerilla Tips of the Day</h3>
        </div>
        <p className="text-sm text-guerilla-200">{tip}</p>
      </div>

      {/* Facts grid */}
      <div className="grid grid-cols-2 gap-3">
        {FACTS.map((f) => (
          <div key={f.title} className="bg-guerilla-950 border border-guerilla-800 rounded-xl p-3">
            <div className="text-xl mb-1">{f.icon}</div>
            <div className="text-xs font-semibold text-guerilla-300 mb-1">{f.title}</div>
            <div className="text-xs text-guerilla-500">{f.text}</div>
          </div>
        ))}
      </div>

      {/* Plant list */}
      <div className="bg-guerilla-950 border border-guerilla-800 rounded-xl p-4">
        <h3 className="text-xs uppercase tracking-widest text-guerilla-500 mb-3">🌿 Robuste planter for Norge</h3>
        <div className="flex flex-wrap gap-2">
          {['Rødkløver 🍀', 'Prestekrage 🌼', 'Blåklokke 🔔', 'Valmue 🌺', 'Kornblomst 💙'].map((p) => (
            <span key={p} className="px-2 py-1 bg-guerilla-900 border border-guerilla-700 rounded-full text-xs text-guerilla-300">{p}</span>
          ))}
        </div>
      </div>

      {/* Attribution */}
      <p className="text-xs text-guerilla-700 text-center">{attribution}</p>
    </div>
  );
}
