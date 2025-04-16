import { createApp } from "vue";
import { createPinia } from "pinia";
import piniaPluginPersistedstate from "pinia-plugin-persistedstate";
import App from "@/App.vue";
import router from "@/router";
import { worker } from "@/mock/browser";

const pinia = createPinia();
pinia.use(piniaPluginPersistedstate);

const app = createApp(App).use(router).use(pinia).mount("#app");

if (import.meta.env.MODE === "development") {
  worker.start();
}
