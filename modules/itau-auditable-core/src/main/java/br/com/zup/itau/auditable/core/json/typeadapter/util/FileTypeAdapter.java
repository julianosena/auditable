package br.com.zup.itau.auditable.core.json.typeadapter.util;

import br.com.zup.itau.auditable.core.json.BasicStringTypeAdapter;

import java.io.File;

/**
 * @author bartosz.walacik
 */
class FileTypeAdapter extends BasicStringTypeAdapter<File> {

    @Override
    public String serialize(File sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public File deserialize(String serializedValue) {
        return new File(serializedValue);
    }

    @Override
    public Class getValueType() {
        return File.class;
    }
}
