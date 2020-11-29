package net.silthus.rccities;

import de.exlll.configlib.annotation.Comment;
import de.exlll.configlib.annotation.ConfigurationElement;
import de.exlll.configlib.configs.yaml.BukkitYamlConfiguration;
import de.exlll.configlib.format.FieldNameFormatters;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class RCCitiesUpgradeConfig extends BukkitYamlConfiguration {

    public RCCitiesUpgradeConfig(Path path) {

        super(path, BukkitYamlProperties.builder().setFormatter(FieldNameFormatters.LOWER_UNDERSCORE).build());
    }
}
