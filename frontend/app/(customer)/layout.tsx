export default function CustomerLayout({ children }: { children: React.ReactNode }) {
    return (
        <div style={{ maxWidth: 1080, width: "100%", margin: "3rem auto", padding: "0 1.5rem" }}>
            {children}
        </div>
    );
}
