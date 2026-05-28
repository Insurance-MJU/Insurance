import type { Metadata } from "next";
import { Geist } from "next/font/google";
import "./globals.css";
import { Providers } from "./providers";

const geist = Geist({ variable: "--font-geist-sans", subsets: ["latin"] });

export const metadata: Metadata = {
    title: "한국생명보험",
    description: "스마트 다이렉트 자동차보험",
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
    return (
        <html lang="ko" className={`${geist.variable} h-full antialiased`}>
            <body className="min-h-full flex flex-col font-sans">
                <Providers>{children}</Providers>
            </body>
        </html>
    );
}
