package com.chessclub;

//import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/players")

public class PlayerController {


    private final PlayerService playerService;
    private final MatchService matchService;

    public PlayerController(PlayerService playerService, MatchService matchService) {
        this.playerService = playerService;
        this.matchService = matchService;
    }


    @PostMapping
    public Player create(@RequestBody Player p) {
        return playerService.createPlayer(p);
    }

    @PutMapping("/{id}")
    public Player update(@PathVariable("id") Long id, @RequestBody Player p) {
        return playerService.updatePlayer(id, p);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }

    @GetMapping("/leaderboard")
    public List<Player> leaderboard() {
        return playerService.getLeaderboard();
    }

    @PostMapping("/match")
    public void match(
            @RequestParam(name = "p1") Long p1,
            @RequestParam(name = "p2") Long p2,
            @RequestParam(name = "result") String result
    ) {
        matchService.recordMatch(p1, p2, result);
    }
}
