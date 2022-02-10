import { AccountDetailsRestClient } from "../clients/account-details-rest-client";
import { AccountDetailsRenderService } from "../service/render/account-details-render-service";
import { AlertService } from "../service/util/alert-service";
import { ToastService } from "../service/util/toast-service";

const alertService = new AlertService();
const toastService = new ToastService();
const accountDetailsRestClient = new AccountDetailsRestClient();
const accountDetailsRenderService = new AccountDetailsRenderService(
    alertService,
    toastService,
    accountDetailsRestClient,
);

accountDetailsRenderService.init();
