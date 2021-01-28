import { ToastService } from "../service/toast-service";
import { AccountDetailsRestClient } from "../clients/account-details-rest-client";
import { AccountDetailsRenderService } from "../service/account-details-render-service";

const toastService = new ToastService();
const accountDetailsRestClient = new AccountDetailsRestClient(toastService);
const accountDetailsRenderService = new AccountDetailsRenderService(toastService, accountDetailsRestClient);

accountDetailsRenderService.init();
