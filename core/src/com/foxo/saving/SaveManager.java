package com.foxo.saving;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.badlogic.gdx.utils.ObjectMap;

public class SaveManager
{
    private boolean encoded = false;
    private FileHandle file = Gdx.files.local("save.json");
    private Save save = getSave();


    public SaveManager(boolean encoded) {
        this.encoded = encoded;
        this.save = getSave();
    }

    public static class Save {
        public ObjectMap<String, Object> data = new ObjectMap<>();
    }

    private Save getSave() {
        Save save = new Save();

        if(file != null && file.exists()) {
            Json json = new Json();
            if(encoded)
                save = json.fromJson(Save.class, Base64Coder.decodeString(file.readString()));
            else
                save = json.fromJson(Save.class, file.readString());
        }
        return save;
    }

    public void saveToJson() {
        Json json = new Json();
        json.setOutputType(OutputType.json);

        if(encoded)
            file.writeString(Base64Coder.encodeString(json.prettyPrint(save)), false);
        else
            file.writeString(json.prettyPrint(save), false);
    }

    @SuppressWarnings("unchecked")
    public <T> T loadDataValue(String key, Class type) {
        if(save.data.containsKey(key))
            return (T) save.data.get(key);
        else
            return null;
    }

    public void saveDataValue(String key, Object object) {
        save.data.put(key, object);
        saveToJson();
    }
}