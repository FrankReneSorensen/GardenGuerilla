package com.gardenguerilla.service;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class GardeningTipService {
    private static final List<String> TIPS = List.of(
        "Rødkløver er robust og perfekt for norsk klima – spir godt i april–juni. 🍀",
        "Prestekragen trives i sol og halvskygge – et ekte guerilla-valg for grøntarealer. 🌼",
        "Blåklokke vokser villig i norsk natur – spre frø nær skog og kanter. 🔔",
        "Valmue spirer raskt etter lett regn – perfekt for asfalt-kanter og tomter. 🌺",
        "Kornblomst er tøff og nydelig – klassisk guerilla-blomst for bymiljø. 💙",
        "Lett regn nå? Perfekt! Frø får naturlig vanning og bedre jordkontakt. 🌧️",
        "Overskyet vær betyr mindre fordamping – spirer slipper stress. ☁️",
        "Unngå planting i sterk vind – tørker ut jord og småplanter raskt. 💨",
        "Frost kan drepe nye spirer. Vent til minst 2°C om natten. ❄️",
        "Seed bombs lages av leire, kompost og frø – kast og glem! 💣"
    );

    public String getDailyTip() {
        int index = (int) (System.currentTimeMillis() / (1000L * 60 * 60 * 24)) % TIPS.size();
        return TIPS.get(index);
    }
}
