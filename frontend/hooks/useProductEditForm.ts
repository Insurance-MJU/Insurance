import { useState, useCallback } from "react";
import type { ProductFormInfo, SelCoverages } from "@/types/product";

export function useProductEditForm() {
    const [step, setStep] = useState(0);
    const [error, setError] = useState("");

    const [info, setInfo] = useState<ProductFormInfo>({
        productCode: "",
        productName: "",
        lineOfBusiness: "PERSONAL_AUTO",
        targetCustomer: "",
        saleStartDate: "",
        saleEndDate: "",
        status: "DESIGNING",
        description: "",
    });

    const [selCoverages, setSelCoverages] = useState<SelCoverages>({});
    const [selRiders, setSelRiders] = useState<Set<number>>(new Set());

    const toggleCoverage = useCallback((id: number) => {
        setSelCoverages(prev => {
            const next = { ...prev };
            if (next[id]) {
                delete next[id];
            } else {
                next[id] = new Set<number>();
            }
            return next;
        });
    }, []);

    const toggleOption = useCallback((covId: number, optId: number) => {
        setSelCoverages(prev => {
            if (!prev[covId]) return prev;
            const opts = new Set(prev[covId]);
            if (opts.has(optId)) {
                opts.delete(optId);
            } else {
                opts.add(optId);
            }
            return { ...prev, [covId]: opts };
        });
    }, []);

    const toggleRider = useCallback((id: number) => {
        setSelRiders(prev => {
            const next = new Set(prev);
            next.has(id) ? next.delete(id) : next.add(id);
            return next;
        });
    }, []);

    const validate = useCallback(() => {
        if (step === 0) {
            if (!info.productCode.trim()) { setError("상품코드를 입력하세요."); return false; }
            if (!info.productName.trim()) { setError("상품명을 입력하세요."); return false; }
            if (!info.saleStartDate)      { setError("판매시작일을 입력하세요."); return false; }
        }
        setError("");
        return true;
    }, [step, info]);

    const handleNext = useCallback(() => {
        if (validate()) setStep(s => s + 1);
    }, [validate]);

    const handlePrev = useCallback(() => {
        if (step > 0) setStep(s => s - 1);
    }, [step]);

    return {
        step, setStep,
        error, setError,
        info, setInfo,
        selCoverages, setSelCoverages,
        selRiders, setSelRiders,
        toggleCoverage, toggleOption, toggleRider,
        validate, handleNext, handlePrev,
    };
}
