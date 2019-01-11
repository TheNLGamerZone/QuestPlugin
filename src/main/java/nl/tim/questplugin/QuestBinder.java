/*
 * Copyright (C) 2019  Tim Anema
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package nl.tim.questplugin;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import java.io.File;

public class QuestBinder extends AbstractModule
{
    private QuestPlugin questPlugin;
    private File configFolder;

    /**
     * Constructor for binder
     * @param questPlugin
     */
    public QuestBinder(QuestPlugin questPlugin, File configFolder)
    {
        this.questPlugin = questPlugin;
        this.configFolder = configFolder;
    }

    public Injector createInjector()
    {
        return Guice.createInjector(this);
    }

    @Override
    protected void configure() {
        this.bind(QuestPlugin.class).toInstance(this.questPlugin);

        // TODO: Make this change according to storage setting
        //this.bind(Storage.class).to(FileStorage.class);

        this.bind(File.class).annotatedWith(Names.named("config")).toInstance(configFolder);
    }
}
