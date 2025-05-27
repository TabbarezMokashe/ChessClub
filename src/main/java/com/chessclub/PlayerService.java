package com.chessclub;

import jakarta.persistence.Entity;
//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service


public class PlayerService {
    private final PlayerRepository playerRepo;
    public PlayerService(PlayerRepository playerRepo) {
        this.playerRepo = playerRepo;
    }

    public Player createPlayer(Player player) {
        int lastRank = playerRepo.findAll().size() + 1;
        player.setRank(lastRank);
        player.setGamesPlayed(0);
        return playerRepo.save(player);
    }

    public Player updatePlayer(Long id, Player updated) {
        Player existing = playerRepo.findById(id).orElseThrow(() -> new RuntimeException("Player with ID " + id + " not found"));

        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setBirthday(updated.getBirthday());
        return playerRepo.save(existing);
    }

    public void deletePlayer(Long id) {
        Player player = playerRepo.findById(id).orElseThrow();
        int removedRank = player.getRank();
        playerRepo.delete(player);

        List<Player> players = playerRepo.findAll();
        for (Player p : players) {
            if (p.getRank() > removedRank) {
                p.setRank(p.getRank() - 1);
                playerRepo.save(p);
            }
        }
    }

    public List<Player> getLeaderboard() {
        return playerRepo.findAllByOrderByRankAsc();
    }
}
