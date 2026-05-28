export interface Product {
    productId: string;
    productCode: string;
    productName: string;
    status: string;
    target: string;
    saleStartDate: string;
    saleEndDate: string;
    description: string;
}

export interface PremiumEstimateResponse {
    basePremium: number;
    finalPremium: number;
    currency: string;
}

export interface Subscription {
    subscriptionNo: string;
    applicantName: string;
    productName: string;
    premium: number;
    status: string;
    subscriptionDate: string;
    contractId?: string;
}

export interface Coverage {
    coverageId: string;
    coverageName: string;
    mandatory: boolean;
    limitOptions: string[];
}

export interface Rider {
    riderCode: string;
    riderName: string;
    discountRate: number;
}

export interface Accident {
    accidentId: string;
    reportedBy: string;
    phone: string;
    accidentDate: string;
    accidentLocation: string;
    accidentDetail: string;
    status: string;
    contractId: string;
}

export interface Investigator {
    employeeId: string;
    name: string;
    specialty: string;
    openCaseCount: number;
}

export interface Claim {
    claimId: string;
    accidentId: string;  // CL-03 선행 확인에 사용
    claimantName: string;
    claimDate: string;
    contractId: string;
    description: string;
    status: string;
    compensationAmount?: number;
    deductibleAmount?: number;
}
