import { AccountDetailsRestClient } from "../clients/account-details-rest-client";
import { AccountDetailsRenderService } from "../service/account-details-render-service";
import { AlertService } from "../service/alert-service";
import { ToastService } from "../service/toast-service";

const alertService = new AlertService();
const toastService = new ToastService();
const accountDetailsRestClient = new AccountDetailsRestClient();
const accountDetailsRenderService = new AccountDetailsRenderService(
    alertService,
    toastService,
    accountDetailsRestClient,
);

accountDetailsRenderService.init();
