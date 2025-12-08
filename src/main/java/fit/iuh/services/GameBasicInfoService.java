package fit.iuh.services;

import fit.iuh.dtos.GameInfoAI;
import fit.iuh.models.Game;
import fit.iuh.models.GameBasicInfo;
import fit.iuh.models.Publisher;
import fit.iuh.repositories.GameBasicInfoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameBasicInfoService {
    private final GameBasicInfoRepository gameRepository;

    public GameBasicInfoService(GameBasicInfoRepository gameRepository) {
        this.gameRepository = gameRepository;
    }


    //Lấy tất cả thông tin cơ bản của các game yêu thích của khách hàng theo customerId
    public List<GameBasicInfo> findAllByGameFavoriteWithCustomerId(String username) {
        return gameRepository.findAllByGameFavoriteWithCustomerId(username);
    }

    public List<GameInfoAI> findInfosByName(String name) {
        return gameRepository.findInfosByName(name);
    }
    public List<GameInfoAI> findByTypeGame(String keyword) {
        return gameRepository.findByTypeGame(keyword);
    }

    public Integer countGamesByPublisher(Publisher publisher) {
        if (publisher == null || publisher.getId() == null) {
            return 0;
        }
        // Gọi Repository để đếm
        // Lấy ID từ đối tượng Publisher
        Long count = gameRepository.countGameBasicInfoByPublisher_Id(publisher.getId());
        return count.intValue();
    }

    public List<GameInfoAI> findBySystem(String os, String cpu, String gpu, Integer ram, Integer storage) {
        return gameRepository.findBySystem(os, cpu, gpu, ram, storage);
    }

    public List<GameInfoAI> searchAdvanced(
            String os,
            String cpu,
            String gpu,
            Integer ram,
            Integer storage,
            String keyword,
            String categoryname,
            Double minRating,
            Double maxPrice
    ){
       return gameRepository.searchAdvanced(
               os,
               cpu,
               gpu,
               ram,
               storage,
               keyword,
               categoryname,
               minRating,
               maxPrice
       );
    }

    public boolean testNameGameAndSystem(
            String namegame,
            String os,
            String cpu,
            String gpu,
            Integer ram,
            Integer storage
    ){
        List<GameInfoAI> gamesByName = findInfosByName(namegame);
        if (gamesByName.isEmpty()) {
            return false; // Không tìm thấy game với tên đã cho
        }

        List<GameInfoAI> gamesBySystem = findBySystem(os, cpu, gpu, ram, storage);

        for (GameInfoAI gameByName : gamesByName) {
            for (GameInfoAI gameBySystem : gamesBySystem) {
                if (gameByName.getId().equals(gameBySystem.getId())) {
                    return true; // Tìm thấy game phù hợp với cả tên và cấu hình hệ thống
                }
            }
        }

        return false; // Không tìm thấy game phù hợp với cả tên và cấu hình hệ thống
    }

}
