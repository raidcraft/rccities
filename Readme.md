# RaidCraft RCCities - Spielerstädte

## Schnellzugriff
* [Spieler-Befehle](#commands-user)

## Features
Dieses Plugin ermöglicht das Verwalten von Spielerstädten.

### Rollen
Jede Spielerstadt benötigt wie im echten Leben einen Bürgermeister. 
Es gibt weitere Rollen um die Stadtverwaltung auf mehrere Spieler zu verteilen. 
Jede Rolle hat unterschiedliche Berechtigungen:

#### Bürgermeister
* Kann die Stadtbeschreibung ändern
* Kann den Stadtspawn neu setzen
* Kann Geld aus der Stadtkasse abheben
* Hat in der Stadt alle sonstigen Berechtigungen
* Kann nicht aus der Stadt geworfen werden
#### Vize Bürgermeister
* Kann neue Plots kaufen
* Kann Rollen verteilen
* Kann Spieler aus der Stadt werfen
#### Assistent
* Kann Plots zuweisen
* Darf überall bauen
* Darf Plot Eigenschaften ändern
#### Einwohner
* Kann sich zum Stadt Spawn teleportieren
* Kann Geld in die Stadtkasse einzahlen
* Darf auf zugewiesenen Plots bauen
#### Sklave
* Kann Geld in die Stadtkasse einzahlen
* Ist zwangsweise Einwohner der Stadt und kann diese nicht verlassen

### Plots
Städte bestehen aus 'Plots' welche jeweils die Größe eines Chunks besitzen. Spieler können neue Plots kaufen und auf der Welt zusammenhängend 'Claimen' um so ihre Stadt zu vergrößern.
Einwohnern können Plots zugewiesen werden um dort zu bauen.

Jede Stadt hat ein Guthaben an Plots, diese können von (Vize-) Bürgermeister auf der Welt verteilt werden. Das Plot-Guthaben kann durch Zukauf, oder den Zugewinn von neuen Einwohnern erhöht werden.

## <a name="commands"></a>Befehle
Die Verwaltung von Städten ist mit Hilfe von Chat-Befehlen möglich.
### <a name="commands-user"></a>Spieler
Am einfachsten stellt man sich beim Ausführen der Befehle in die betroffene Stadt oder Plot. Alternativ kann man die Stadt oder den Plot auch manuell als Paramter mit angeben.
#### Stadt
##### Info Anzeigen
``/town`` oder ``/town info <Stadtname>``
##### Serverstädte Auflisten
``/town list``
##### Spieler in eigene Stadt einladen
``/town invite <Spielername>``
* Der Spieler muss Online sein
* Die Einladung muss bestätigt werden
##### Einladung annehmen
``/town accept``
##### Stadt verlassen
``/town leave``
##### Spieler aus Stadt werfen
``/town kick <Spielername>``
##### An den Stadtspawn teleportieren
``/town spawn``
##### Stadtspawn neu setzen
``/town setspawn``
* Es wird die aktuelle Position des Spielers übernommen
##### Beschreibung der Stadt ändern
``/town setdesc <Beschreibung>``
* Die Beschreibung wird unter ``/town info`` angezeigt
* Die Beschreibung darf Leerzeichen enthalten
##### Geld in Stadtkasse einzahlen
``/town deposit <Betrag>``
##### Geld aus Stadtkasse abheben
``/town withdraw <Betrag>``
* Die Stadtkasse kann nicht negativ belastet werden
##### Eigenschaften von Städten ändern
``/town flag <Flag> <Wert>``
* Folgende Flags stehen zur Auswahl:
    * ``GREETINGS`` ``ON|OFF`` Schaltet die Begrüßung beim betreten von Plots ein oder aus
    * ``LEAF_DECAY`` ``ON|OFF`` Verhindert/Erlaubt das verschwinden von Blättern ohne Stamm
    * ``MOB_SPWANING`` ``ON|OFF`` Verhindert/Erlaubt das Spawnen von Mobs
    * ``PVP`` ``ON|OFF`` Verbietet/Erlaubt PVP im Stadtgebiet
    * ``JOIN_COSTS`` ``Geldbetrag`` Legt den nötigen Betrag fest um Einwohner der Stadt werden zu können
#### Plot
##### Info Anzeigen
``/plot`` oder ``/plot info <Plotname>``
##### <a name="command-plot-claim">Claimen
``/plot claim``
* Plots müssen aneinandern anknüpfen
* Es darf keine andere Region an der Stelle existieren
* Zwischen Städten muss mindestens ein Plot Abstand eingehalten werden
##### Zusätzliche Plots Kaufen
``/plot buy <Anzahl>``
* Der kauf muss bestätigt werden
* Der Befehl kann auch genutzt werden um sich den Preis anzeigen zu lassen
* Neu gekaufte Plots werden dem Plot Guthaben gutgeschrieben und können anschließend [geclaimed](#command-plot-claim) werden
##### Plot Anwohnern zuweisen
``/plot give <Spielername>``
* Der Spieler muss Einwohner der Stadt sein
* Der Spieler hat dadurch Baurechte in dem kompletten Plot
##### Plot Anwohnern wegnehmen
``/plot take <Spielername>``
* Der Spieler hat darauf im Plot keine Baurechte mehr
#### Plot markieren (Fakeln)
``/plot mark``
* Die Begrenzung des Plots wird mit Fakeln markiert (sofern möglich)
* Das Markieren des Plots kostet Geld (um das kostenlose Generieren von Fakeln zu unterbinden)
#### Plot Markierung entfernen
``/plot unmark``
* Es werden alle Fakeln auf der Plot Begrenzung entfernt - unabhängig ob von Spielern oder ``/plot mark`` gesetzt
* Das entfernen von Markierungen ist kostenlos
#### Einwohner
##### Info Anzeigen
``/resident`` oder ``/resident <Spielername>``
##### Zum Bürgermeister benennen
``/resident setmayor <Spielername>``
##### Zum Vize Bürgermeister benennen
``/resident setvicemayor <Spielername>``
##### Zum Assistent benennen
``/resident setassistant <Spielername>``
##### Zum normalen Einwohner benennen
``/resident setresident <Spielername>``
### Admins
Admins können grundsätzlich bei den meisten Befehlen mit Hilfe der Flag ``-f`` am Ende des Befehls die Ausführung erzwingen, auch wenn sie keine Mitglieder der Stadt sind.
#### Stadt
##### Stadt Gründen
``/town create <Stadtname>``
* Stadtnamen sollten keine Leerzeichen enthalten
* Zum Beispiel: NewEden, NewRaidCity, Wallenstein
##### Neue Stadt den Spielern übergeben
Hierfür muss nur der Bürgermeister ernannt werden. 
``/town setmayor <Stadtname> <Spielername> -f``
##### Stadt Löschen
Vor dem Löschen der Stadt sollten evtl. alle Plots Unclaimed werden um den Ursprungszustand wieder herzustellen. 
``/Plot unclaim -ra``. 
Die Stadt selbst wird folgendermaßen gelöscht. 
``/town delete <Stadtname>``
* Das Löschen der Stadt muss nochmals bestätigt werden.
* Ein Wiederherstellen der Stadt ist nicht möglich!
#### Plot
##### Unlcaim
``/plot unlcaim [-ra]``
* ``-r`` Stellt den Ursprungszustand des Plots vor dem Claimen her (Chunk wird mit Backup Schematic ersetzt)
* ``-a`` Unclaimed ALLE Plots der Stadt
## Permissions
Es gibt zwei Hauptgruppen:  
* ``rccities.user``
* ``rccities.admin``

Einzelne Befehle lassen sich auch granularer steuern:
* ``rccities.user.<Typ>.<Befehl>``

Z.B. ``rccities.user.plot.claim``
