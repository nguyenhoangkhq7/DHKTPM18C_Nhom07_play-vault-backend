package fit.iuh.services;

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

    public Integer countGamesByPublisher(Publisher publisher) {
        if (publisher == null || publisher.getId() == null) {
            return 0;
        }
        // Gọi Repository để đếm
        // Lấy ID từ đối tượng Publisher
        Long count = gameRepository.countGameBasicInfoByPublisher_Id(publisher.getId());
        return count.intValue();
    }

}
