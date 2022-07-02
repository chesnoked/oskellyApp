package su.reddot.presentation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import su.reddot.domain.service.wishlist.WishListException;
import su.reddot.domain.service.wishlist.WishListService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/wishlist")
public class WishListController {

    private final WishListService wishListService;

    @PutMapping("/add")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> addToWishList(@RequestParam("productId") Long productId, UserIdAuthenticationToken token) {
        try {
            wishListService.addProduct(productId, token.getUserId());
        } catch (WishListException e) {
            log.error(e.getLocalizedMessage(), e);
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{productId}")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> removeFromWishList(@PathVariable("productId") Long productId, UserIdAuthenticationToken token) {
        try {
            wishListService.removeProduct(productId, token.getUserId());
        } catch (WishListException e) {
            log.error(e.getLocalizedMessage(), e);
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }

        return ResponseEntity.ok().build();
    }

}
