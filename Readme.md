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
##### Geld in Stadtkasse einzahlen
``/town deposit <Betrag>``
##### Geld aus Stadtkasse abheben
``/town withdraw <Betrag>``
#### Plot
##### Plots Zukaufen

#### Einwohner
### Admins
Admins können grundsätzlich bei den meisten Befehlen mit Hilfe der Flag ``-f`` am Ende des Befehls die Ausführung erzwingen, auch wenn sie keine Mitglieder der Stadt sind.
#### Stadt
##### Stadt Gründen
``/town create <Stadtname>``
* Stadtnamen sollten keine Leerzeichen enthalten
* Zum Beispiel: NewEden, NewRaidCity, Wallenstein
##### Neue Stadt den Spielern übergeben
Hierfür muss nur der Bürgermeister ernannt werden:
``/town setmayor <Stadtname> <Spielername> -f``
##### Stadt Löschen
``/town delete <Stadtname>``
* Das Löschen der Stadt muss nochmals bestätigt werden.
#### Plot
#### Einwohner
