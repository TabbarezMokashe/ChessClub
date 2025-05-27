package com.chessclub;

//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service

public class MatchService {

    private final PlayerRepository playerRepo;

    public MatchService(PlayerRepository playerRepo) {
        this.playerRepo = playerRepo;
    }


    public void recordMatch(Long id1, Long id2, String result) {
        Player p1 = playerRepo.findById(id1)
                .orElseThrow(() -> new RuntimeException("Player with ID " + id1 + " not found"));
        Player p2 = playerRepo.findById(id2)
                .orElseThrow(() -> new RuntimeException("Player with ID " + id2 + " not found"));

        p1.setGamesPlayed(p1.getGamesPlayed() + 1);
        p2.setGamesPlayed(p2.getGamesPlayed() + 1);

        int rank1 = p1.getRank();
        int rank2 = p2.getRank();

        List<Player> players = playerRepo.findAll();

        if ((result.equals("P1_WIN") && rank1 > rank2) ||
                (result.equals("P2_WIN") && rank2 > rank1)) {

            Player higher = rank1 < rank2 ? p1 : p2;
            Player lower = rank1 < rank2 ? p2 : p1;

            int originalHighRank = higher.getRank();
            int originalLowRank = lower.getRank();

            // Step 1: Demote higher-ranked player by 1
            int newHighRank = originalHighRank + 1;
            higher.setRank(newHighRank);

            // Step 2: Promote whoever was at newHighRank to originalHighRank
            for (Player pl : players) {
                if (!pl.getId().equals(higher.getId()) && pl.getRank() == newHighRank) {
                    pl.setRank(originalHighRank); // promote to higher’s old position
                }
            }

            // Step 3: Lower-ranked winner moves up by half the difference
            int moveUp = (originalLowRank - originalHighRank) / 2;
            int newLowRank = originalLowRank - moveUp;

            for (Player pl : players) {
                if (!pl.getId().equals(lower.getId()) &&
                        pl.getRank() >= newLowRank && pl.getRank() < originalLowRank) {
                    pl.setRank(pl.getRank() + 1); // shift down
                }
            }

            lower.setRank(newLowRank);
        }

        else if (result.equals("DRAW")) {
            Player lower = rank1 > rank2 ? p1 : p2;
            Player higher = rank1 > rank2 ? p2 : p1;

            if (Math.abs(lower.getRank() - higher.getRank()) > 1) {
                int newRank = lower.getRank() - 1;

                for (Player pl : players) {
                    if (!pl.getId().equals(lower.getId()) &&
                            pl.getRank() >= newRank && pl.getRank() < lower.getRank()) {
                        pl.setRank(pl.getRank() + 1); // shift down
                    }
                }

                lower.setRank(newRank);
            }
        }

        // Normalize: sort & assign 1-based ranks again
        players = playerRepo.findAll();
        players.sort(Comparator.comparingInt(Player::getRank));
        for (int i = 0; i < players.size(); i++) {
            Player pl = players.get(i);
            pl.setRank(i + 1);
            playerRepo.save(pl);
        }
    }



}
