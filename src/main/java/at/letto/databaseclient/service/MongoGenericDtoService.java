package at.letto.databaseclient.service;

import at.letto.databaseclient.modelMongo.DtoGeneric;
import at.letto.databaseclient.repository.mongo.letto.MongoDtoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MongoGenericDtoService {
    @Autowired
    private MongoDtoRepository mongoDTORepository;

    // Speichern eines generischen DTOs
    public <T> void saveDTO(T dto, String idTemp) {
        DtoGeneric<T> mongoDTO = new DtoGeneric<>();
        mongoDTO.setDtoData(dto); // Das Original-DTO
        mongoDTO.setIdTemp(dto.getClass().getSimpleName()+"-"+idTemp); // Temporäre ID setzen
        mongoDTO.setClassInfo(dto.getClass().getSimpleName());
        mongoDTORepository.save(mongoDTO); // Speichern in MongoDB
    }

    // Abrufen eines generischen DTOs
    public <T> T getDTOByIdTemp(String idTemp, Class dtoClass) {
        DtoGeneric<T> mongoDTO = mongoDTORepository.findByIdTemp(dtoClass.getSimpleName()+"-"+idTemp);
        if (mongoDTO != null) {
            return mongoDTO.getDtoData(); // Nur das ursprüngliche DTO zurückgeben (ohne idTemp)
        }
        return null; // Falls nicht gefunden
    }

    public void removeDTOByIdTemp(String idTemp, Class dtoClass) {
        mongoDTORepository.deleteById(dtoClass.getSimpleName()+"-"+idTemp);
    }

    /** Abrufen aller DTOs einer Klasse */
    public <T> List<T> getAllDTOsByClass(Class dtoClass) {
        List<DtoGeneric<T>> mongoDTOs = mongoDTORepository.findAllByClassInfo(dtoClass.getSimpleName());
        return mongoDTOs.stream()
                .map(DtoGeneric::getDtoData)
                .collect(Collectors.toList());
    }
}