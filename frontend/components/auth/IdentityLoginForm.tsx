'use client';

import { useState } from "react";
import { useRouter } from "next/navigation";
import VerifyForm from "@/components/enrollment/steps/VerifyForm";
import { loginByIdentity, saveSession, saveIdentityToken } from "@/queries/auth";
import { ApiError } from "@/queries/api";

export default function IdentityLoginForm() {
    const [name, setName] = useState("");
    const [ssnFront, setSsnFront] = useState("");
    const [ssnBack, setSsnBack] = useState("");
    const [phone, setPhone] = useState("");
    const [verificationMethod, setVerificationMethod] = useState("PASS");
    const [verificationSessionId, setVerificationSessionId] = useState<string | null>(null);
    const router = useRouter();

    const combinedSsn = `${ssnFront}${ssnBack}`;

    const saveSessionAndRedirect = (res: any) => {
        saveSession(res);
        if (res.role === "ADMIN" || res.role === "EMPLOYEE") {
            router.push("/employee/dashboard");
        } else {
            router.push("/");
        }
        router.refresh();
    };

    const handleVerifiedToken = async (token: string) => {
        try {
            const res = await loginByIdentity(token);
            saveIdentityToken(token);
            saveSessionAndRedirect(res);
        } catch (error) {
            if (error instanceof ApiError) {
                alert(`본인인증 로그인 실패: ${error.message}`);
            } else {
                alert("본인인증 로그인 중 오류가 발생했습니다.");
            }
        }
    };

    return (
        <div className="flex flex-col gap-4">
            <input
                type="text"
                value={name}
                onChange={(e) => setName(e.target.value)}
                className="w-full border p-2 rounded"
                placeholder="이름"
            />

            <div className="grid grid-cols-2 gap-2">
                <input
                    type="text"
                    value={ssnFront}
                    onChange={(e) => setSsnFront(e.target.value.replace(/\D/g, "").slice(0, 6))}
                    className="w-full border p-2 rounded"
                    placeholder="주민번호 앞 6자리"
                    inputMode="numeric"
                    maxLength={6}
                />
                <input
                    type="password"
                    value={ssnBack}
                    onChange={(e) => setSsnBack(e.target.value.replace(/\D/g, "").slice(0, 7))}
                    className="w-full border p-2 rounded"
                    placeholder="주민번호 뒤 7자리"
                    inputMode="numeric"
                    maxLength={7}
                />
            </div>

            <input
                type="text"
                value={phone}
                onChange={(e) => setPhone(e.target.value)}
                className="w-full border p-2 rounded"
                placeholder="휴대폰번호"
            />

            <VerifyForm
                name={name}
                ssn={combinedSsn}
                phone={phone}
                verificationMethod={verificationMethod}
                verificationSessionId={verificationSessionId}
                onVerificationMethodChange={setVerificationMethod}
                onVerificationSessionChange={setVerificationSessionId}
                onVerifiedToken={handleVerifiedToken}
            />
        </div>
    );
}
