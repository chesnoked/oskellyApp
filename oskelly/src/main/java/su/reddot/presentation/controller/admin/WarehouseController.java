package su.reddot.presentation.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import su.reddot.domain.model.product.ProductItem;
import su.reddot.domain.service.admin.warehouse.WarehouseService;
import su.reddot.domain.service.user.UserService;
import su.reddot.infrastructure.security.token.UserIdAuthenticationToken;

@Controller
@RequiredArgsConstructor
@PreAuthorize("hasAuthority(T(su.reddot.domain.model.enums.AuthorityName).PRODUCT_MODERATION)")
@RequestMapping("/admin/warehouse")
public class WarehouseController {

    private final WarehouseService warehouseService;
    private final UserService userService;

    @GetMapping
    public String getRootPage(Model m, UserIdAuthenticationToken token){
        //FIXME нет проверки на пустой набор
        m.addAttribute("waitingItems",warehouseService.getItemsByState(ProductItem.State.SALE_CONFIRMED));
        return "admin/hq-warehouse/warehouse-base";
    }

    @GetMapping("/sold")
    public String getWarehouseSoldPage(Model m) {
        //FIXME нет проверки на пустой набор
        m.addAttribute("onStockItems", warehouseService.getSoldItems());
        return "admin/hq-warehouse/warehouse-on-stock";
    }

    @GetMapping("/cleaning")
    public String getCleaningPage() {return "admin/hq-warehouse/warehouse-cleaning"; }

    @GetMapping("/ready-to-ship")
    public String getReadyToShiPage(Model m) {
        //FIXME нет проверки на пустой набор
        m.addAttribute("readyToShipItems",warehouseService.getReadyToShipItems());
        return "admin/hq-warehouse/warehouse-ready-to-ship";
    }

    @PutMapping("/taketowarehouse")
    public ResponseEntity<?> takeToWarehouse(@RequestParam Long itemId){
        try {
            warehouseService.takeToWarehouse(itemId);
            return ResponseEntity.ok(itemId);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e);
        }

    }

    @PutMapping("/startverification")
    public ResponseEntity<?> startVerification(@RequestParam Long itemId){
        try {
            warehouseService.startVerification(itemId);
            return ResponseEntity.ok(itemId);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e);
        }

    }

    @PutMapping("/finalyverification")
    public ResponseEntity<?> finalyVerification(@RequestParam Long itemId, @RequestParam String state){

        try {

            ProductItem.State resultState = null;
            //тут будут несколько возможных сосояний возвращаться
            //todo венсти в сервис
            switch(state){
                case "VERIFICATION_OK": resultState = ProductItem.State.VERIFICATION_OK; break;
                case "VERIFICATION_NEED_CLEANING": resultState = ProductItem.State.VERIFICATION_NEED_CLEANING; break;
                case "VERIFICATION_BAD_STATE_NEED_CONFIRMATION": resultState = ProductItem.State.VERIFICATION_BAD_STATE_NEED_CONFIRMATION; break;
                case "VERIFICATION_BAD_STATE_BUYER_CONFIRMED": resultState = ProductItem.State.VERIFICATION_BAD_STATE_BUYER_CONFIRMED; break;
                case "REJECTED_AFTER_VERIFICATION" : resultState = ProductItem.State.REJECTED_AFTER_VERIFICATION; break;
                case "READY_TO_SHIP": resultState = ProductItem.State.READY_TO_SHIP; break;
            }
            if (resultState == null) {
                throw new IllegalArgumentException("Product Item с ID " + itemId + "переводится в недопустимое состояние после верификации");
            }
            warehouseService.stopVerification(itemId, resultState);
            return ResponseEntity.ok(itemId);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e);
        }

    }

    @PutMapping("/setreadytoship")
    public ResponseEntity<?> setReadyToShip(@RequestParam Long itemId){
        try {
            warehouseService.markAsReadyToShip(itemId);
            return ResponseEntity.ok(itemId);
        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e);
        }
    }


    @PutMapping("/createwaybilltobuyer")
    public ResponseEntity<?> callLogisticServiceForPickUp(@RequestParam Long itemId){

        try {

            warehouseService.createWaybillToClient(itemId);
            return ResponseEntity.ok("For Product Item:" + itemId + " State set to: " + ProductItem.State.CREATE_WAYBILL_TO_BUYER);

        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e);
        }
    }

    @PutMapping("/sendtobuyer")
    public ResponseEntity<?> sendItemToBuyer(@RequestParam Long itemId){

        try {

            warehouseService.sendToClient(itemId);
            return ResponseEntity.ok("For Product Item:" + itemId + " State set to: " + ProductItem.State.SHIPPED_TO_CLIENT);

        }catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e);
        }
    }



}
