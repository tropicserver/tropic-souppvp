package gg.tropic.souppvp.qna

import gg.scala.commons.scheme.impl.SinglePageSchemedMenu
import gg.tropic.souppvp.profile.profile
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 7/1/2023
 */
object QnAMenu
{
    private val qnaSchema = SinglePageSchemedMenu()
        .title("Commonly asked questions....")
        .pattern(
            "XXXOBGXXX"
        )
        .manuallyClosed { _, player ->
            if (
                player.profile.initialQnAMenuOpen == false ||
                player.profile.initialQnAMenuOpen == null
                )
            {
                player.sendMessage("${CC.B_GREEN}You can view QnA questions on demand using /qna!")
                player.sendMessage("${CC.WHITE}Have fun playing on our soup gamemode!")

                player.playSound(
                    player.location,
                    Sound.LEVEL_UP,
                    1.0f, 1.0f
                )

                player.profile.initialQnAMenuOpen = true
                player.profile.save()
            }
        }
        .map('X') { _, _ ->
            PaginatedMenu.PLACEHOLDER
        }
        .map('O') { _, _ ->
            ItemBuilder
                .of(Material.PAPER)
                .name("${CC.AQUA}${
                    listOf("Does growly skid?", "Is jay real?", "Can you cheat?", "Are there kits?", "Are donkies present?", "What year is it?", "Who is ScalaStudios").random()
                }?")
                .addToLore("${CC.GRAY}${
                    listOf("Yes", "No", "Maybe", "14").random()
                }.")
                .toButton()
        }
        .map('B') { _, _ ->
            ItemBuilder
                .of(Material.PAPER)
                .name("${CC.AQUA}How long do ground-items exist?")
                .addToLore(
                    "${CC.GRAY}Both death drops and manually dropped",
                    "${CC.GRAY}items expire in ${CC.WHITE}8 seconds${CC.GRAY}."
                )
                .toButton()
        }
        .map('G') { _, _ ->
            ItemBuilder
                .of(Material.PAPER)
                .name("${CC.AQUA}Are there duels?")
                .addToLore(
                    "${CC.GRAY}The server does not have duels.",
                    "${CC.WHITE}Join ${CC.BOLD}Duels${CC.WHITE} to duel a player."
                )
                .toButton()
        }
        .compose()

    fun qna(player: Player) = qnaSchema.createAndOpen(player)
}
