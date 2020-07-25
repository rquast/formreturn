package com.ebstrada.formreturn.manager.ui.reprocessor.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FragmentRecognitionStructure;

public class RecognitionStructureMap
    extends java.util.HashMap<String, RecognitionStructureComparator> {

    private static final long serialVersionUID = 1L;

    public void addStructure(FragmentRecognitionStructure recognitionStructure) {

        String fieldname = recognitionStructure.getFieldName();

        RecognitionStructureComparator recognitionStructureComparator = get(fieldname);
        if (recognitionStructureComparator == null) {
            addRecognitionStructure(fieldname, recognitionStructure,
                recognitionStructure.getOrderIndex());
        } else {
            recognitionStructureComparator.addData(recognitionStructure);
        }

    }

    private void addRecognitionStructure(String fieldname, FragmentRecognitionStructure data,
        int order) {
        RecognitionStructureComparator recognitionStructureComparator =
            new RecognitionStructureComparator();
        recognitionStructureComparator.setFieldname(fieldname);
        recognitionStructureComparator.setOrder(order);
        recognitionStructureComparator.addData(data);
        put(fieldname, recognitionStructureComparator);
    }

    public List<RecognitionStructureComparator> getSortedData() {
        List<RecognitionStructureComparator> values =
            new ArrayList<RecognitionStructureComparator>(values());
        Collections.sort(values);
        return values;
    }

}
