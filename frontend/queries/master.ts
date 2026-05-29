import { fetchApi } from "./api";

// GET은 Insurance 백엔드 실제 엔드포인트 사용
export async function getCoverages() {
    const res = await fetchApi("/coverages");
    const list: any[] = Array.isArray(res) ? res : (res?.data ?? []);
    return list.map((c: any) => ({
        ...c,
        id:                     c.coverageId   ?? c.id,
        name:                   c.coverageName ?? c.name,
        coverageTypeDisplayName: c.coverageType ?? c.coverageName ?? '',
        mandatory:               c.mandatory ?? false,
        limitOptions:            c.limitOptions ?? [],
        exclusions:              c.exclusions ?? [],
    }));
}

export async function getRiders() {
    const res = await fetchApi("/riders");
    const list: any[] = Array.isArray(res) ? res : (res?.data ?? []);
    return list.map((r: any) => ({
        ...r,
        id:           r.riderCode   ?? r.id,
        riderCode:    r.riderCode   ?? r.id,
        name:         r.riderName   ?? r.name,
        riderName:    r.riderName   ?? r.name,
        discountRate: r.discountRate ?? 0,
    }));
}

// 아래는 Insurance 백엔드에 미구현 - 추후 백엔드 확장 필요
export async function createCoverage(data: any) {
    return fetchApi("/master/coverages", { method: "POST", body: JSON.stringify(data) });
}

export async function getCoverage(id: string) {
    return fetchApi(`/master/coverages/${id}`);
}

export async function updateCoverage(id: string, data: any) {
    return fetchApi(`/master/coverages/${id}`, { method: "PUT", body: JSON.stringify(data) });
}

export async function deleteCoverage(id: string) {
    return fetchApi(`/master/coverages/${id}`, { method: "DELETE" });
}

export async function getBaseRates(type?: string) {
    const url = type ? `/master/base-rates?type=${encodeURIComponent(type)}` : "/master/base-rates";
    return fetchApi(url).then(r => r.data);
}

export async function createBaseRate(data: any) {
    return fetchApi("/master/base-rates", { method: "POST", body: JSON.stringify(data) });
}

export async function updateBaseRate(id: number, data: any) {
    return fetchApi(`/master/base-rates/${id}`, { method: "PUT", body: JSON.stringify(data) });
}

export async function deleteBaseRate(id: number) {
    return fetchApi(`/master/base-rates/${id}`, { method: "DELETE" });
}

export async function getBaseRateStats(type?: string) {
    const url = type ? `/master/base-rates/stats?type=${encodeURIComponent(type)}` : "/master/base-rates/stats";
    return fetchApi(url).then(r => r.data);
}

export async function getExclusions() {
    return fetchApi("/master/exclusions").then(r => r.data);
}

export async function createExclusion(data: any) {
    return fetchApi("/master/exclusions", { method: "POST", body: JSON.stringify(data) });
}

export async function deleteExclusion(id: number) {
    return fetchApi(`/master/exclusions/${id}`, { method: "DELETE" });
}

export async function getRider(id: string) {
    return fetchApi(`/master/riders/${id}`);
}

export async function createRider(data: any) {
    return fetchApi("/master/riders", { method: "POST", body: JSON.stringify(data) });
}

export async function updateRider(id: string, data: any) {
    return fetchApi(`/master/riders/${id}`, { method: "PUT", body: JSON.stringify(data) });
}

export async function deleteRider(id: string) {
    return fetchApi(`/master/riders/${id}`, { method: "DELETE" });
}

export async function getProvisions() {
    return fetchApi("/master/provisions").then(r => r.data);
}

export async function getProvision(id: number) {
    return fetchApi(`/master/provisions/${id}`);
}

export async function createProvision(data: any) {
    return fetchApi("/master/provisions", { method: "POST", body: JSON.stringify(data) });
}

export async function deleteProvision(id: number) {
    return fetchApi(`/master/provisions/${id}`, { method: "DELETE" });
}

export async function getProvisionItems() {
    return fetchApi("/master/provisions/items").then(r => r.data);
}

export async function getProvisionTree(id: number) {
    return fetchApi(`/master/provisions/${id}/items`).then(r => r.data);
}

export async function addProvisionItem(provisionId: number, data: any) {
    return fetchApi(`/master/provisions/${provisionId}/items`, { method: "POST", body: JSON.stringify(data) });
}

export async function updateProvisionItem(itemId: number, data: any) {
    return fetchApi(`/master/provisions/items/${itemId}`, { method: "PUT", body: JSON.stringify(data) });
}

export async function deleteProvisionItem(itemId: number) {
    return fetchApi(`/master/provisions/items/${itemId}`, { method: "DELETE" });
}
