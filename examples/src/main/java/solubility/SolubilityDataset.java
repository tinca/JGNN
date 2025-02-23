package solubility;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tinca.chem.api.Structure;
import com.tinca.chem.io.smiles.importer.SmilesImporter;
import mklab.JGNN.adhoc.Dataset;
import mklab.JGNN.core.Matrix;
import mklab.JGNN.core.matrix.SparseMatrix;
import mklab.JGNN.core.tensor.DenseTensor;

/**
 * Encloses data of a single structure graph:
 * - adjacency
 * - node features:
 *      - chemical element
 * - edge features:
 *      - bond order
 * - structure features:
 *      - physico-chemical properties
 */
public class SolubilityDataset extends Dataset {
    private static final Logger LOG = LoggerFactory.getLogger(SolubilityDataset.class);
    private static final int ID = 0;
    private static final int SMILES = 1;
    private static final int SOLUBILITY = 2;

    private final List<MoleculeRecord> records;

    SolubilityDataset() {
        String localPath = "downloads/solubility/solubility.csv";
        downloadIfNotExists(localPath,
                "https://github.com/tinca/data/raw/main/solubility/curated-reduced.csv");
        records = readLines(localPath);
        // TODO
    }

    private List<MoleculeRecord> readLines(String file) {
        List<MoleculeRecord> records = new ArrayList<>(5000);

        String line = null;
        int recordCount = 1;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // header
            line = reader.readLine();

            while (line != null) {
                String[] cols = line.split(",");
                importStructure(cols[SMILES], cols[ID]).ifPresent(structure -> {
                    var record = new MoleculeRecord(
                            cols[ID],
                            cols[SMILES],
                            structure,
                            Double.parseDouble(cols[SOLUBILITY]));
                    records.add(record);
                });

                line = reader.readLine();
                recordCount++;
            }
        } catch (Exception e) {
            LOG.error("Dataset loading error: {} \nline: {}", e.getMessage(), line);
        }

        LOG.info("Records read: {}", recordCount);
        return records;
    }

    private Optional<Structure> importStructure(String smilesContent, String id) {
        try {
            var doc = new SmilesImporter().read(smilesContent);
            if (!doc.getStructures().isEmpty()) {
                return Optional.of(doc.getStructures().get(0));
            }
            else if (!doc.getMixtures().isEmpty()) {
                LOG.warn("Mixture found - {} : {}", id, smilesContent);
            }
        } catch (Throwable e) {
            LOG.warn("SMILES import error: {} for {}: {}", e.getMessage(), id, smilesContent);
        }

        return Optional.empty();
    }

    private Matrix createAdjacency() {
//        var size = molecule.getAtoms().size();
//        var matrix = new SparseMatrix(size, size);
        // rows: indices of atoms
        // cols: indices of atoms that connect with the atom of a given row

        // TODO
//        var ctab = molecule.getCtab();
//
//        for (int row = 0; row < ctab.length; row++) {
//            for (int j = 0; j < ctab[row].length; j++) {
//                int col = ctab[row][j];
//                if (j < col) {
//                    // https://github.com/MKLab-ITI/JGNN/blob/main/tutorials/Data.md#constructing-graph-adjacency-matrices
//                    matrix.put(row, col, 1);
//                    matrix.put(col, row, 1);
//                }
//            }
//        }

//        return matrix;
        return null;
    }

    private Matrix createNodeFeatures() {
//        var atoms = molecule.getAtoms();
//        var tensor = new DenseTensor(atoms.size());
//        // TODO
////        atoms.forEach(a -> tensor.put(molecule.indexOf(a), a.getAtno()));
//
//        return tensor.asColumn().toDense();
        return null;
    }

    private record MoleculeRecord(String id, String smiles, Structure molecule, double solubility) {}
}
