package su.reddot.presentation.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import su.reddot.domain.service.admin.order.AdminOrderService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/orders")
public class OrderAdminController {

    private final AdminOrderService adminOrderService;

    @GetMapping
    public String baseMethod(){
        return "redirect:/admin/orders/active";
    }

    /**
     * Список всех активных(в состоянии  HOLD)
     * @return
     */
    @GetMapping("/active")
    public String getActiveOrders(Model model){

        model.addAttribute("activeOrders", adminOrderService.findAllActive());
        return "admin/orders/active-orders";
    }

    @GetMapping("/need-attention")
    public String getOrdersThatNeedAttention(Model model){

        model.addAttribute("warningOrders", adminOrderService.findAllInWarningState());
        return "admin/orders/need-attention";
    }
}
